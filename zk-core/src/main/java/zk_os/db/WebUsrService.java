package zk_os.db;

import lombok.Getter;
import lombok.SneakyThrows;
import mp.utl_ndb.SqlDbUrl;
import mp.utl_odb.DBU;
import mpc.env.APP;
import mpe.NT;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import utl_spring.AppContext;
import zk_os.AppZosCore;
import zk_os.db.net.*;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

//@Service(value = "userDetailsService")
public class WebUsrService implements UserDetailsService {

	@Autowired
	private @Getter WebUsrRepository webUsrRepo;

	private static WebUsrService webUsrService = null;

	public static WebUsrService get() {
		return webUsrService != null ? webUsrService : (webUsrService = (WebUsrService) AppContext.getBean(UserDetailsService.class));
	}

	public static WebUsr findAppUsrRoot() {
		return findAppUsrSID(APP.getNetName(), RootWebUsr.SID);
	}

//	private static WebUsr findWebUsrSID(long sid) {
//		return get().webUsrRepo.loadUserBySid(sid);
//	}

	public static WebUsr findAppUsrSID(String net, long sid) {
		return get().webUsrRepo.loadUserBySid(net, sid);
	}

//	public static WebUsr findWebUsrID(long id, WebUsr... defRq) {
//		Optional<WebUsr> byId = get().webUsrRepo.findById(id);
//		return ARG.toDefThrowOptMsg(() -> X.f("Except user by id '%s'", id), byId, defRq);
//	}

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
		WebUsr usr = webUsrService.webUsrRepo.loadAllActiveUsers(login, phc);
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
		Pare<WebUsr, String> webUsr = RootWebUsr.init();
		save(webUsr.key());
		return webUsr.val();
	}

	public WebUsr save(WebUsr s) {
		return webUsrRepo.save(s);
	}

	private static final Semaphore LOCK = new Semaphore(1);

	@SneakyThrows
	public void updateIfEmptyNameOrHasDiff(WebUsr usr, String name) {
		if (X.notEmpty(usr.getFirst_name()) || X.empty(name)) {
			return;
		}
		if (name.equals(usr.getFirst_name())) {
			return;
		}
		usr.setFirst_name(name);
		webUsrService.save(usr);
	}

	@SneakyThrows
	public @NotNull WebUsr createAndSaveNewWebUsr(NT nt, long nid, String name) {
		LOCK.acquire();
		try {
			return createAndSaveNewWebUsr_SingleThread(nt, nid, name);
		} finally {
			LOCK.release();
		}
	}

	private @NotNull WebUsr createAndSaveNewWebUsr_SingleThread(NT nt, long nid, String name) throws SQLException {

		WebUsr webUsr = new NetWebUsr();

		webUsr.setLogin(nt.toNetShortPfxLogin(nid));
		webUsr.setAlias(webUsr.getLogin());
		webUsr.setNet(nt.name());
		webUsr.setMainRole(ROLE.EDITOR);
		webUsr.setFirst_name(name);
		webUsr.setNid(nid);

		String maxValueString = DBU.getMaxValueString(SqlDbUrl.ofFile(AppZosCore.getAppDbFile()), WebUsr.class, CN.SID);
		Long nextSid = UST.LONG(maxValueString);
		webUsr.setSid(++nextSid);

		save(webUsr);

		if (Sec.L.isInfoEnabled()) {
			Sec.L.info("Created new NET user:" + webUsr);
		}

		createAndSaveNewWebUsr_App_SingleThread(webUsr);

		return webUsr;
	}

	private @NotNull WebUsr createAndSaveNewWebUsr_App_SingleThread(WebUsr webUsrNet) throws SQLException {

		WebUsr webUsr = new AppWebUsr();

		webUsr.setLogin(webUsrNet.getLogin());
		webUsr.setAlias(webUsrNet.getAlias());
		webUsr.setNet(AppWebUsr.getDefaultNetName());
		webUsr.setMainRole(ROLE.EDITOR);

		String firstName = X.empty(webUsrNet.getFirst_name()) ? AppWebUsr.getDefaultUserName() : webUsrNet.getFirst_name();
		webUsr.setFirst_name(firstName);

//		String maxValueString = DBU.getMaxValueString(SqlDbUrl.ofFile(AppZosCore.getAppDbFile()), WebUsr.class, CN.SID);
//		Long nextSid = UST.LONG(maxValueString);
		webUsr.setSid(webUsrNet.getSid());

//		webUsr.setNid(webUsr.getSid());

		save(webUsr);

		if (Sec.L.isInfoEnabled()) {
			Sec.L.info("Created new APP user:\n{}\nfrom:\n{}", webUsr, webUsrNet);
		}
		return webUsr;
	}

}
