package zk_os.db;

import mpu.IT;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.STR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import utl_spring.AppContext;
import zk_os.db.net.RootZkosWebUsr;
import zk_os.db.net.WebUsr;
import zk_os.db.net.SimpleBearWebUsr;
import zk_os.sec.Sec;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.List;

//@Service(value = "userDetailsService")
public class WebUsrService implements UserDetailsService {

	@Autowired
	private WebUsrRepository webUsrRepo;

	private static WebUsrService webUsrService = null;

	public static WebUsrService get() {
		return webUsrService != null ? webUsrService : (webUsrService = (WebUsrService) AppContext.getBean(UserDetailsService.class));
	}

	public static WebUsr findWebUsrRoot() {
		return findWebUsrSid(RootZkosWebUsr.ROOT_SID);
	}

	public static WebUsr findWebUsrSid(long sid) {
		return ARRi.first(WebUsrService.get().webUsrRepo.findAllById(ARR.as(sid)));
	}


	public WebUsr loadUserByLogin(String login, WebUsr... defRq) throws UsernameNotFoundException {
		WebUsrService webUsrService = WebUsrService.get();
		WebUsr usr = webUsrService.webUsrRepo.findWebUserByName(login);
		if (usr != null) {
			return usr;
		}
		return ARG.toDefRq(defRq);
	}

	@Override
	public WebUsr loadUserByUsername(String login) throws UsernameNotFoundException {
		WebUsr usr = loadUserByLogin(login, null);
		if (usr != null) {
			return usr;
		} else if (Sec.Mode.LP_BEAR.isEnable()) {
			return new SimpleBearWebUsr();
		}
		throw new UsernameNotFoundException("User not found");
	}

	public List<WebUsr> loadUserByNetNidAndAllAlias(String net, long nid) {
		return webUsrRepo.loadUserByNidAndAllAlias(net, nid);
	}

	public WebUsr loadUserByNetNid(String net, long nid) {
		return webUsrRepo.loadUserByNidAndAnyAlias(net, nid);
	}

	public WebUsr loadUserByNetNidAlias(String net, long nid, String alias) {
		return webUsrRepo.loadUserByNetNidAndAlias(net, nid, alias);
	}

	public List<WebUsr> loadUserByAlias(String alias, boolean checkCleanName) {
		if (alias == null) {
			return ARR.EMPTY_LIST;
		}
		return webUsrRepo.loadUserByAlias(checkCleanName ? IT.isFilename(alias) : alias);
	}

	public WebUsr loadUserById(Long id) {
		return webUsrRepo.loadUserById(IT.NN(id, "user Id is null"));
	}

	public List<WebUsr> loadUserByNetAlias(String net, String alias) {
		return webUsrRepo.loadUserByNetAlias(net, alias);
	}

	public static WebUsr findWebUsr(String login, String password, boolean isPhc, WebUsr... defRq) {
		WebUsrService webUsrService = WebUsrService.get();
		String phc = isPhc ? password : Sec.Phc.getPassHashCode(password);
		WebUsr usr = webUsrService.webUsrRepo.findAllActiveUsers(login, phc);
		if (usr != null) {
			return usr;
		}
		return ARG.toDefRq(defRq);
	}

	//	private DataSource dataSource;
	//	private NamedParameterJdbcOperations namedTemplate;
	//	private NamedParameterJdbcTemplate namedTemplate;

	@PostConstruct
	public void init() {
		//		this.namedTemplate = new NamedParameterJdbcTemplateSqlHistoryWrapper(new NamedParameterJdbcTemplate(dataSource));
		//		this.namedTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public String initDB() {
		if (webUsrRepo.existsById(1L)) {
			Iterable<WebUsr> allById = webUsrRepo.findAllById(ARR.as(1L));
			return "FirstInit:false:" + webUsrRepo.getLastUser() + ":" + ARRi.first(allById).getLast_name();
		}
		Pare<WebUsr, String> webUsr = RootZkosWebUsr.init();
		save(webUsr.key());
		return webUsr.val();
	}

	public WebUsr save(WebUsr s) {
		return webUsrRepo.save(s);
	}


//	@Autowired
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}
//
//	public NamedParameterJdbcTemplate getNamedTemplate() {
//		return namedTemplate;
//	}
//
//	public DataSource getDataSource() {
//		return dataSource;
//	}
//
//	private static final String COMMON_QUERY = "select %s from %s fs where (" +
//											   "fs.docid=(" +
//											   "select docid from DC_SP_UserAccess where " +
//											   "( data_startactive is NULL OR now() >= data_startactive ) " +
//											   "AND ( data_endactive is NULL OR now() < data_endactive ) " +
//											   "AND login IN (:logins)" +
//											   ") " +
//											   ")";
//	private static final String COMMON_QUERY_1 = "select %s from %s fs where ( fs.docid=( select docid from DC_SP_UserAccess where ( data_startactive is NULL OR now() >= data_startactive )  AND ( data_endactive is NULL OR now() < data_endactive )  AND login IN (:logins))";

//	private static String COL_QUERY(String fsTablenName, String colname) {
//		return String.format(COMMON_QUERY, colname, fsTablenName);
//	}


	/**
	 * --------------------------API--------------------------
	 */
//	public Optional<List<String>> gogogo() {
//		System.out.println("gooooooooooooo:" + repo);
//		return Optional.of(Arrays.asList("wtf"));
//	}
//	public static void ztest() {
//		BasicDataSource datasource = new BasicDataSource();
//		datasource.setDriverClassName("com.mysql.jdbc.Driver");
//		datasource.setUrl("jdbc:mysql://127.0.0.1");
//		datasource.setUsername("username");
//		datasource.setPassword("password");
//
//		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(datasource);
//		template.queryForObject("SELECT COUNT(*) FROM table_name WHERE key ='\'?'", EmptySqlParameterSource.INSTANCE, Integer.class);
//	}


}
