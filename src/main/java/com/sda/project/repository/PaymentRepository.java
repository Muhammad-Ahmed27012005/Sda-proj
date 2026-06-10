package com.sda.project.repository;

import com.sda.project.model.Payment;
import com.sda.project.model.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByUserOrderByPaymentDateDesc(User user);

	@Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentStatus = 'SUCCESS'")
	BigDecimal totalSuccessfulRevenue();

	@Query("""
		select year(p.paymentDate), month(p.paymentDate), coalesce(sum(p.amount), 0)
		from Payment p
		where p.paymentStatus = 'SUCCESS'
		group by year(p.paymentDate), month(p.paymentDate)
		order by year(p.paymentDate) desc, month(p.paymentDate) desc
		""")
	List<Object[]> monthlyRevenue();
}
