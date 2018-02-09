package dajana;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import dajana.model.User;

public interface UserRepository extends CrudRepository<User, String> {
	

	List<User> findByEmail(String email);
	
	List<User> findByTitle(String title);
	
	@Query("select u from User u where email like ':kw%' or title like ':kw%' or userName like ':kw%'")
	List<User> fetchUsers(@Param("kw") String kw);

}
