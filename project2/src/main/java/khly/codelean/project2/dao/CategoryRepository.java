package khly.codelean.project2.dao;

import khly.codelean.project2.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {

}
