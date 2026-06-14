package com.sda.project.repository;

import com.sda.project.model.Video;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends JpaRepository<Video, Long> {
	Optional<Video> findByImdbId(String imdbId);
	List<Video> findTop10ByOrderByUploadDateDesc();

	@Query("""
		select v from Video v
		where (:genre is null or lower(v.genre) = lower(:genre))
		  and (:year is null or v.releaseYear = :year)
		  and (:rating is null or v.rating >= :rating)
		  and (:search is null or lower(v.title) like lower(concat('%', :search, '%'))
		    or lower(v.description) like lower(concat('%', :search, '%')))
		order by v.uploadDate desc
		""")
	List<Video> search(
			@Param("genre") String genre,
			@Param("year") Integer year,
			@Param("rating") BigDecimal rating,
			@Param("search") String search,
			Pageable pageable);

	@Query("""
		select v from Video v left join v.watchHistory h
		group by v
		order by count(h) desc, v.uploadDate desc
		""")
	List<Video> findTrending(Pageable pageable);
}
