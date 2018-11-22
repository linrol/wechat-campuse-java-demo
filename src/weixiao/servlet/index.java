package weixiao.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import weixiao.util.HttpUtil;

/***
 * 
 * @author 罗林 邮箱：1071893649@qq.com（如有不懂，欢迎骚扰） 环境：jre1.8 tomcat
 *         v7.0（注：本Demo依赖fastJson lib包，如没有请自行下载）
 *         请求示列地址：http://www.example.com/appDemo/index
 *
 */
@WebServlet("/index")
public class index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 微校提供
	 */
	public static final String API_KEY = "BE30B2BA30ABFADB";
	public static final String API_SECRET = "CF8CDD8D0B7B1ACDA76201F406BED81E";

	/**
	 * 公众号基本信息接口
	 */
	public static final String WEIXIAO_MEDIA_INFO = "http://weixiao.qq.com/common/get_media_info";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		switch (request.getParameter("type")) {
		case "open":
			open(request, response);
			break;
		case "close":
			close(request, response);
			break;
		case "config":
			config(request, response);
			break;
		case "monitor":
			monitor(request, response);
			break;
		case "trigger":
			trigger(request, response);
			break;
		case "keyword":
			keyword(request, response);
			break;
		default:
			break;
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 开启应用
	 * 
	 * @return
	 * @throws IOException
	 */
	public void open(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> map = getPostParameter(request);
		String sign = map.remove("sign");
		String calSign = calSign(map);
		/** 验证签名 */
		if (calSign.equals(sign)) {
			Long localTime = Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));
			Long wxTime = Long.parseLong((map.get("timestamp")));
			long timeS = localTime - wxTime;
			/** 检查时间戳 */
			if (timeS >= 0 && timeS < 10) {
				// 对新关键词进行记录和同步用来做消息处理

				/** OK返回正确的json格式数据 */
				response.getWriter().write("{\"errcode\":0,\"errmsg\":\"\",\"is_config\":1}");
			} else {
				response.getWriter().write("{\"errcode\":5003,\"errmsg\":\"请求接口失败\"}");// 可能是重放攻击
			}
		} else {
			response.getWriter().write("{\"errcode\":5004,\"errmsg\":\"签名错误\"}");// 签名错误
		}
	}

	/**
	 * 关闭应用
	 * 
	 * @return
	 */
	public void close(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> map = getPostParameter(request);
		String sign = map.remove("sign");
		String calSign = calSign(map);
		/** 验证签名 */
		if (calSign.equals(sign)) {
			Long localTime = Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));
			Long wxTime = Long.parseLong((map.get("timestamp")));
			long timeS = localTime - wxTime;
			/** 检查时间戳 */
			if (timeS >= 0 && timeS < 10) {
				// 此处做自己想做的操作...
				response.getWriter().write("{\"errcode\":0,\"errmsg\":\"\"}");
			} else {
				response.getWriter().write("{\"errcode\":5003,\"errmsg\":\"请求接口失败\"}");// 可能是重放攻击
			}
		} else {
			response.getWriter().write("{\"errcode\":5004,\"errmsg\":\"签名错误\"}");// 签名错误
		}
	}

	/**
	 * 应用配置页
	 * 
	 * @return
	 */
	public void config(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("media_id", request.getParameter("media_id"));
		map.put("platform", request.getParameter("platform"));
		map.put("timestamp", request.getParameter("timestamp"));
		map.put("nonce_str", request.getParameter("nonce_str"));
		String sign = request.getParameter("sign");
		String calSign = calSign(map);
		/** 验证签名 */
		if (calSign.equals(sign)) {
			// 将mediaId生成自己的token，将$token放入cookie

			// 跳转应用配置页
			response.sendRedirect("https://www.weiniekeji.com");
		} else {
			response.getWriter().write("{\"errcode\":5004,\"errmsg\":\"签名错误\"}");// 签名错误
		}
	}

	/**
	 * 应用监控
	 * 
	 * @return
	 */
	public void monitor(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getWriter().write(request.getParameter("echostr"));
	}

	/**
	 * 应用触发
	 * 
	 * @return
	 */
	public void trigger(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 加载应用的页面
		response.sendRedirect("https://www.weiniekeji.com");
	}

	/**
	 * 消息回复类支持模糊匹配的应用需提供此接口
	 */
	public void keyword(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> map = getPostParameter(request);
		String keyword = map.remove("keyword");
		String sign = map.remove("sign");
		String calSign = calSign(map);
		/** 验证签名 */
		if (calSign.equals(sign)) {
			Long localTime = Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));
			Long wxTime = Long.parseLong((map.get("timestamp")));
			long timeS = localTime - wxTime;
			/** 检查时间戳 */
			if (timeS >= 0 && timeS < 10) {
				// 此处做自己想做的操作,记录keyword...
				System.out.println(keyword);
				response.getWriter().write("{\"errcode\":0,\"errmsg\":\"\"}");
			} else {
				response.getWriter().write("{\"errcode\":5003,\"errmsg\":\"请求接口失败\"}");// 可能是重放攻击
			}
		} else {
			response.getWriter().write("{\"errcode\":5004,\"errmsg\":\"签名错误\"}");// 签名错误
		}
	}

	/**
	 * 获取POST参数
	 */
	public Map<String, String> getPostParameter(HttpServletRequest request) {
		Map<String, String> requestMap = new TreeMap<String, String>();
		Map<String, String[]> map = request.getParameterMap();
		JSONObject json = null;
		for (String paramterName : map.keySet()) {
			if (paramterName.indexOf("media_id") != -1) {
				json = JSON.parseObject(paramterName);
				requestMap.put("api_key", json.getString("api_key"));
				requestMap.put("media_id", json.getString("media_id"));
				requestMap.put("nonce_str", json.getString("nonce_str"));
				requestMap.put("timestamp", json.getString("timestamp"));
				if (json.getString("keyword") != null) {
					requestMap.put("keyword", json.getString("keyword"));
				}
				requestMap.put("sign", json.getString("sign"));
			}
		}
		return requestMap;
	}

	/**
	 * 计算签名
	 * 
	 * @param map
	 * @return
	 */
	public static String calSign(Map<String, String> map) {
		// 将 mediaId, apiKey, timeStamp, nonceStr四个参数进行字典序排序
		String str = "";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			str += entry.getKey() + "=" + entry.getValue() + "&";
		}
		str += "key=" + API_SECRET;
		return md5Encode(str).toUpperCase();
	}

	/**
	 * MD5
	 * 
	 * @param srcString
	 * @return
	 */
	public final static String md5Encode(String srcString) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = srcString.getBytes("UTF-8");
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(strTemp);
			byte[] mdByte = messageDigest.digest();
			int j = mdByte.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = mdByte[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取公众号基本信息
	 * 
	 * @return
	 */
	public String getMediaInfo(String mediaId) {
		String nonceStr = getRandomString(32);
		String timestamp = String.valueOf(System.currentTimeMillis()).toString().substring(0, 10);
		Map<String, String> map = new TreeMap<String, String>();
		map.put("api_key", API_KEY);
		map.put("media_id", mediaId);
		map.put("timestamp", timestamp);
		map.put("nonce_str", nonceStr);
		map.put("sign", calSign(map));
		return HttpUtil.doPost(WEIXIAO_MEDIA_INFO, JSON.toJSONString(map));
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
}
