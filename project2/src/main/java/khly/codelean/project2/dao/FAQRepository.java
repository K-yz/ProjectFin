package khly.codelean.project2.dao;


import khly.codelean.project2.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
}
