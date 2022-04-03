package org.jeecg.common.constant;

/**
 * @author: huangxutao
 * @date: 2019-06-14
 * @description: 缓存常量
 */
public interface CacheConstant {

	/**
	 * 字典信息缓存（含禁用的字典项）
	 */
    public static final String SYS_DICT_CACHE = "sys:cache:dict";

	/**
	 * 字典信息缓存 status为有效的
	 */
	public static final String SYS_ENABLE_DICT_CACHE = "sys:cache:dictEnable";
	/**
	 * 表字典信息缓存
	 */
    public static final String SYS_DICT_TABLE_CACHE = "sys:cache:dictTable";
	public static final String SYS_DICT_TABLE_BY_KEYS_CACHE = SYS_DICT_TABLE_CACHE + "ByKeys";

	/**
	 * 数据权限配置缓存
	 */
    public static final String SYS_DATA_PERMISSIONS_CACHE = "sys:cache:permission:datarules";

	/**
	 * 缓存用户信息
	 */
	public static final String SYS_USERS_CACHE = "sys:cache:user";

	/**
	 * 全部部门信息缓存
	 */
	public static final String SYS_DEPARTS_CACHE = "sys:cache:depart:alldata";


	/**
	 * 全部部门ids缓存
	 */
	public static final String SYS_DEPART_IDS_CACHE = "sys:cache:depart:allids";


	/**
	 * 测试缓存key
	 */
	public static final String TEST_DEMO_CACHE = "test:demo";

	/**
	 * 字典信息缓存
	 */
	public static final String SYS_DYNAMICDB_CACHE = "sys:cache:dbconnect:dynamic:";

	/**
	 * gateway路由缓存
	 */
	public static final String GATEWAY_ROUTES = "sys:cache:cloud:gateway_routes";


	/**
	 * gateway路由 reload key
	 */
	public static final String ROUTE_JVM_RELOAD_TOPIC = "gateway_jvm_route_reload_topic";

	/**
	 * TODO 冗余代码 待删除
	 *插件商城排行榜
	 */
	public static final String PLUGIN_MALL_RANKING = "pluginMall::rankingList";
	/**
	 * TODO 冗余代码 待删除
	 *插件商城排行榜
	 */
	public static final String PLUGIN_MALL_PAGE_LIST = "pluginMall::queryPageList";


	/**
	 * online列表页配置信息缓存key
	 */
	public static final String ONLINE_LIST = "sys:cache:online:list";

	/**
	 * online表单页配置信息缓存key
	 */
	public static final String ONLINE_FORM = "sys:cache:online:form";

	/**
	 * online报表
	 */
	public static final String ONLINE_RP = "sys:cache:online:rp";

	/**
	 * online图表
	 */
	public static final String ONLINE_GRAPH = "sys:cache:online:graph";
	/**
	 * 用户账户锁缓存KEY
	 */
	public static final String LOGIN_PASSWORD_ERROR_KEY = "bc-u:login-password-error:%s"; // 登陆密码错误时间缓存key $uid值为用户ID，格式为集合，成员为错误时间，失效时间为2小时
	public static final String LOGIN_LOCK_KEY = "bc-u:login-lock:%s"; // 登陆锁定缓存key $uid值为用户ID 失效时间2小时

	public static String getLoginPasswordErrorKey(String uuid) {
		return String.format(LOGIN_PASSWORD_ERROR_KEY, uuid);
	}

	public static String getLoginLockKey(String uuid) {
		return String.format(LOGIN_LOCK_KEY, uuid);
	}
}
