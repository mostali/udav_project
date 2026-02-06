package zk_os.db;


//import org.springframework.data.jpa.repository.Query;

import mp.utl_odb.netapp.mdl.NetUserModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import zk_os.db.net.WebUsr;

import java.util.List;

//https://www.baeldung.com/spring-boot-data-sql-and-schema-sql
//https://www.baeldung.com/spring-data-jpa-query
@Repository
public interface WebUsrRepository extends CrudRepository<WebUsr, Long> {
	//	@Query("SELECT u FROM web_usr u WHERE u.login = ?1 and u.phc = ?2")
//	@Query("SELECT u FROM web_usr limit 1")
//	List<WebUsr> findAllActiveUsers(String login, String phc);
//	@Query(value = "SELECT * FROM web_usr u WHERE u.login =:login and u.phc =:phc")
//	WebUsr findAllActiveUsers(@Param("login") String login, @Param("phc") String phc);
//	WebUsr findAllActiveUsers();

	//	@Query(value = "select * from t_user where name like %?1%", nativeQuery = true)
	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.login =?1", nativeQuery = true)
	WebUsr findWebUserByName(String name);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.net =?1 AND u.nid =?2 AND alias NOT NULL", nativeQuery = true)
	List<WebUsr> loadUserByNidAndAllAlias(String net, Long nid);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.net =?1 AND u.nid =?2", nativeQuery = true)
	WebUsr loadUserByNidAndAnyAlias(String net, Long nid);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.net =?1 AND u.nid =?2 AND alias =?3", nativeQuery = true)
	WebUsr loadUserByNetNidAndAlias(String net, Long nid, String alias);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE alias =?1", nativeQuery = true)
	List<WebUsr> loadUserByAlias(String alias);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE id =?1 limit 1", nativeQuery = true)
	WebUsr loadUserById(Long id);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.net =?1 AND alias =?2", nativeQuery = true)
	List<WebUsr> loadUserByNetAlias(String net, String alias);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.login =?1 and u.phc =?2", nativeQuery = true)
	WebUsr loadAllActiveUsers(String name, String phc);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.sid = ( SELECT MAX(id) FROM " + NetUserModel.TABLE + " )", nativeQuery = true)
	WebUsr getLastUser();

//	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.sid =?1", nativeQuery = true)
//	WebUsr loadUserBySid(Long sid);

	@Query(value = "SELECT * FROM " + NetUserModel.TABLE + " u WHERE u.net=?1 and u.sid =?2", nativeQuery = true)
	WebUsr loadUserBySid(String net, Long sid);

//	@Query(value = "SELECT MAX(sid) FROM usr")
//	Long getMaxUid();

//	@Query(
//			value = "SELECT * FROM Users u WHERE u.status = ?1",
//			nativeQuery = true)
//	WebUsr findUserByStatusNative(Integer status);

}

