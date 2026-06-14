package com.sda.project.controller;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sda.project.dto.ApiResponse;
import com.sda.project.dto.PaymentResultDTO;
import com.sda.project.dto.SubscribeDTO;
import com.sda.project.model.Subscription;
import com.sda.project.model.User;
import com.sda.project.patterns.command.CancelSubscriptionCommand;
import com.sda.project.patterns.command.SubscribePlanCommand;
import com.sda.project.patterns.command.VideoCommandInvoker;
import com.sda.project.service.PaymentService;
import com.sda.project.service.SubscriptionService;
import com.sda.project.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
	private final SubscriptionService subscriptionService;
	private final PaymentService paymentService;
	private final UserService userService;
	private final VideoCommandInvoker invoker;

	public SubscriptionController(SubscriptionService subscriptionService, PaymentService paymentService, UserService userService, VideoCommandInvoker invoker) {
		this.subscriptionService = subscriptionService;
		this.paymentService = paymentService;
		this.userService = userService;
		this.invoker = invoker;
	}

	@PostMapping("/subscribe")
	public ApiResponse<Map<String, Object>> subscribe(@Valid @RequestBody SubscribeDTO request, Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		PaymentResultDTO payment = paymentService.processSubscriptionPayment(userId, request.planName(), request.paymentMethod());
		// DESIGN PATTERN: Command
		invoker.executeCommand(new SubscribePlanCommand(subscriptionService, userId, request.planName()));
		Subscription subscription = subscriptionService.activeSubscription(userId).orElseThrow();
		return ApiResponse.ok("Subscription active", Map.of(
				"payment", payment,
				"subscription", safeSubscription(subscription)));
	}

	@PostMapping("/cancel")
	public ApiResponse<Void> cancel(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		// DESIGN PATTERN: Command
		invoker.executeCommand(new CancelSubscriptionCommand(subscriptionService, userId));
		return ApiResponse.ok("Subscription cancelled", null);
	}

	@GetMapping("/status")
	public ApiResponse<Map<String, Object>> status(Authentication authentication) {
		Long userId = userService.currentUser(authentication).getUserId();
		Subscription subscription = subscriptionService.activeSubscription(userId).orElse(null);
		return ApiResponse.ok("Subscription status loaded", Map.of(
				"active", subscription != null,
				"subscription", subscription == null ? "none" : safeSubscription(subscription)));
	}

	/** Generate and stream a PDF payment slip for the active subscription */
	@GetMapping("/slip")
	public void downloadSlip(Authentication authentication, HttpServletResponse response) throws IOException {
		User user = userService.currentUser(authentication);
		Subscription sub = subscriptionService.activeSubscription(user.getUserId())
				.orElseThrow(() -> new IllegalStateException("No active subscription found"));

		BigDecimal amount = subscriptionService.priceFor(sub.getPlanName());
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"streamflixtv-slip-" + sub.getSubscriptionId() + ".pdf\"");

		// ── Build PDF ──────────────────────────────────────────
		Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
		PdfWriter.getInstance(doc, response.getOutputStream());
		doc.open();

		Color darkBg   = new Color(14, 14, 15);
		Color accentGr = new Color(143, 254, 9);
		Color white    = new Color(241, 241, 238);
		Color mutedGr  = new Color(100, 100, 105);

		Font fontHeader  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  28, white);
		Font fontSubHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  11, accentGr);
		Font fontBody    = FontFactory.getFont(FontFactory.HELVETICA,       10, white);
		Font fontMuted   = FontFactory.getFont(FontFactory.HELVETICA,        9, mutedGr);
		Font fontBig     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  22, accentGr);
		Font fontLabel   = FontFactory.getFont(FontFactory.HELVETICA,        9, mutedGr);
		Font fontValue   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  10, white);

		// ── Header banner ──────────────────────────────────────
		PdfPTable headerBanner = new PdfPTable(1);
		headerBanner.setWidthPercentage(100);
		PdfPCell bannerCell = new PdfPCell();
		bannerCell.setBackgroundColor(darkBg);
		bannerCell.setBorder(Rectangle.NO_BORDER);
		bannerCell.setPadding(24);

		Paragraph brand = new Paragraph("StreamFlixTv", fontHeader);
		brand.setAlignment(Element.ALIGN_LEFT);
		bannerCell.addElement(brand);

		Paragraph tagline = new Paragraph("SUBSCRIPTION PAYMENT SLIP", fontSubHead);
		tagline.setAlignment(Element.ALIGN_LEFT);
		tagline.setSpacingBefore(4);
		bannerCell.addElement(tagline);

		headerBanner.addCell(bannerCell);
		doc.add(headerBanner);

		// ── Green accent line ──────────────────────────────────
		PdfPTable accentLine = new PdfPTable(1);
		accentLine.setWidthPercentage(100);
		PdfPCell accentCell = new PdfPCell(new Phrase(" "));
		accentCell.setBackgroundColor(accentGr);
		accentCell.setBorder(Rectangle.NO_BORDER);
		accentCell.setFixedHeight(4);
		accentLine.addCell(accentCell);
		doc.add(accentLine);

		// ── Amount block ───────────────────────────────────────
		PdfPTable amountBlock = new PdfPTable(1);
		amountBlock.setWidthPercentage(100);
		PdfPCell amountCell = new PdfPCell();
		amountCell.setBackgroundColor(new Color(22, 22, 24));
		amountCell.setBorder(Rectangle.NO_BORDER);
		amountCell.setPaddingTop(20);
		amountCell.setPaddingBottom(20);
		amountCell.setPaddingLeft(28);

		Paragraph amtLabel = new Paragraph("TOTAL CHARGED", fontLabel);
		amountCell.addElement(amtLabel);
		Paragraph amtValue = new Paragraph("$" + amount.toPlainString(), fontBig);
		amtValue.setSpacingBefore(2);
		amountCell.addElement(amtValue);
		amountBlock.addCell(amountCell);
		doc.add(amountBlock);

		doc.add(new Paragraph(" "));

		// ── Details table ──────────────────────────────────────
		PdfPTable details = new PdfPTable(new float[]{3, 5});
		details.setWidthPercentage(100);
		details.setSpacingBefore(8);

		addRow(details, "Subscriber",      user.getFullName(),                     fontLabel, fontValue, darkBg, white);
		addRow(details, "Email",           user.getEmail(),                        fontLabel, fontValue, new Color(18,18,20), white);
		addRow(details, "Plan",            sub.getPlanName().name(),               fontLabel, fontValue, darkBg, white);
		addRow(details, "Status",          sub.getStatus().name(),                 fontLabel, fontValue, new Color(18,18,20), white);
		addRow(details, "Start Date",      sub.getStartDate().format(fmt),         fontLabel, fontValue, darkBg, white);
		addRow(details, "Renewal Date",    sub.getEndDate().format(fmt),           fontLabel, fontValue, new Color(18,18,20), white);
		addRow(details, "Subscription ID", String.valueOf(sub.getSubscriptionId()), fontLabel, fontValue, darkBg, white);
		addRow(details, "Issued",          LocalDate.now().format(fmt),             fontLabel, fontValue, new Color(18,18,20), white);

		doc.add(details);

		// ── Footer note ────────────────────────────────────────
		doc.add(new Paragraph(" "));
		doc.add(new Paragraph(" "));
		PdfPTable footerTable = new PdfPTable(1);
		footerTable.setWidthPercentage(100);
		PdfPCell footerCell = new PdfPCell();
		footerCell.setBackgroundColor(new Color(10, 10, 12));
		footerCell.setBorder(Rectangle.BOX);
		footerCell.setBorderColor(new Color(40, 40, 45));
		footerCell.setPadding(16);
		footerCell.addElement(new Paragraph(
				"This slip is an auto-generated receipt for your StreamFlixTv subscription. "
				+ "Keep it for your records. For billing queries contact support@streamflixtv.com",
				fontMuted));
		footerTable.addCell(footerCell);
		doc.add(footerTable);

		doc.close();
	}

	// ── Helpers ────────────────────────────────────────────────────────────────

	private Map<String, Object> safeSubscription(Subscription subscription) {
		return Map.of(
				"subscriptionId", subscription.getSubscriptionId(),
				"planName", subscription.getPlanName(),
				"startDate", subscription.getStartDate().toString(),
				"endDate", subscription.getEndDate().toString(),
				"status", subscription.getStatus());
	}

	private void addRow(PdfPTable table, String label, String value,
			Font labelFont, Font valueFont, Color bg, Color borderColor) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
		labelCell.setBackgroundColor(bg);
		labelCell.setBorderColor(new Color(35, 35, 38));
		labelCell.setPadding(10);

		PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "—", valueFont));
		valueCell.setBackgroundColor(bg);
		valueCell.setBorderColor(new Color(35, 35, 38));
		valueCell.setPadding(10);

		table.addCell(labelCell);
		table.addCell(valueCell);
	}
}
