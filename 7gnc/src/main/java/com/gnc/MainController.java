package com.gnc;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.gnc.dao.ArDao;
import com.gnc.dao.ArfishDao;
import com.gnc.dao.ArygDao;
import com.gnc.dao.AvallDao;
import com.gnc.dao.CenterDao;
import com.gnc.dao.DeviceDao;
import com.gnc.dao.FishDao;
import com.gnc.dao.LessonsDao;
import com.gnc.dao.LibraryDao;
import com.gnc.dao.UserDao;
import com.gnc.dao.YgDao;
import com.gnc.dto.ArfishDto;
import com.gnc.dto.ArygDto;
import com.gnc.dto.AvallDto;
import com.gnc.dto.Criteria;
import com.gnc.dto.Criteria2;
import com.gnc.dto.ExcelData;
import com.gnc.dto.FishDto;
import com.gnc.dto.PageDTO;
import com.gnc.dto.Paging;
import com.gnc.dto.Paging2;
import com.gnc.dto.YgDto;
import com.gnc.dto.lect_listDto;
import com.gnc.service.kpiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class MainController {

	String key;
	int masterId;
	String masterResult;
	String masterCenterId;
	int masterLessonCenterId;
	String testKeyword;
	int masterArDeviceId;
	int masterCount = 1;
	long masterStart;
	long masterEnd;
	String masterYear;

	@Autowired
	UserDao userDao;

	@Autowired
	LessonsDao lessonsDao;

	@Autowired
	CenterDao centerDao;

	@Autowired
	ArDao arDao;

	@Autowired
	LibraryDao libraryDao;

	@Autowired
	DeviceDao deviceDao;

	@Autowired
	FishDao fishDao;

	@Autowired
	YgDao ygDao;

	@Autowired
	ArfishDao arfishDao;

	@Autowired
	ArygDao arygDao;

	@Autowired
	AvallDao avallDao;

	@Autowired
	kpiService kpiService;

	@ExceptionHandler(value = Exception.class)
	public Map<String, String> handleException(Exception e) {
		Map<String, String> res = new HashMap<>();
		res.put("errorMsg", e.getMessage());
		res.put("status", "error");
		return res;
	}

	@GetMapping("/")
	public String root(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		return "redirect:/admin";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

	@GetMapping("/survey-login")
	public String surveyLoginForm() {
		return "surveyLogin";
	}

	@PostMapping("/login")
	public @ResponseBody int login(@RequestBody String result, BindingResult bindingResult,
			HttpServletRequest request) {
		LocalDateTime now = LocalDateTime.now();
		String UID = "";
		String PSWD = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		UID = element.getAsJsonObject().get("UID").getAsString();
		PSWD = element.getAsJsonObject().get("PSWD").getAsString();

		String coverted = new String(DatatypeConverter.parseBase64Binary(PSWD));

		coverted += "6b51d431df5d7f141cbececcf79edf3dd861c3b4069f0b11661a3eefacbba918";

		String check = userDao.getUserAccount(UID, coverted);
		if (System.currentTimeMillis() < masterEnd) {
			return 3;
		}

		if (check == null) {

			if (masterCount == 5) {
				masterEnd = System.currentTimeMillis() + 60 * 1000;

				if (System.currentTimeMillis() < masterEnd) {
					masterCount = 1;
					return 3;
				}
				masterCount = 1;
			} else {
				masterCount++;
				return 2;
			}
		}

		else if (System.currentTimeMillis() > masterEnd && check != null) {

			HttpSession session = request.getSession();
			session.setAttribute(SessionConstants.LOGIN_MEMBER, check);
			userDao.last_lgn_dtDao(check, now);
			masterCount = 0;
			return 1;
		}

		return 3;

	}

	private void String(byte[] decodeBase64) {
		// TODO Auto-generated method stub

	}

	@PostMapping("/survey-login")
	public String surveyLogin(@RequestBody String result, BindingResult bindingResult, HttpServletRequest request) {
		LocalDateTime now = LocalDateTime.now();
		String UID = "";
		String PSWD = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		UID = element.getAsJsonObject().get("UID").getAsString();
		PSWD = element.getAsJsonObject().get("PSWD").getAsString();

		String check = userDao.suerveyGetUserAccount(UID, PSWD);
		masterCenterId = userDao.suerveyCenterId(UID, PSWD);

		if (check == null) {
			bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
			return "surveyLogin";
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(SessionConstants.LOGIN_MEMBER2, check);

			return "redirect:/survey_list";
		}

	}

	@PostMapping("/logout")
	public String logout(HttpServletRequest request,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		LocalDateTime now = LocalDateTime.now();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(SessionConstants.LOGIN_MEMBER);

			// sesssion.invalidate();

		}
		userDao.use_dtDao(check, now);
		return "redirect:/login";

	}

	@PostMapping("/survey-logout")
	public String surveyLogout(HttpServletRequest request1,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER2, required = false) String check) {

		LocalDateTime now = LocalDateTime.now();
		HttpSession session1 = request1.getSession(false);
		if (session1 != null) {
			session1.removeAttribute(SessionConstants.LOGIN_MEMBER2);
		}

		return "redirect:/survey-login";

	}

	@GetMapping("/admin")
	public String home(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("list", lessonsDao.lessonsListDao(cri));

		Paging pageMaker = new Paging();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(lessonsDao.getCount());

		model.addAttribute("pageMaker", pageMaker);

		if (lessonsDao.getCount() == 0) {
			model.addAttribute("check", 0);
		} else {
			model.addAttribute("check", 1);
		}

		return "admin01_list";
	}

	@GetMapping("/survey_list")
	public String survey_list(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER2, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model) {
		if (check == null) {
			return "redirect:/survey-login";
		}
		if (lessonsDao.surveyLessonsListDao(cri, masterCenterId).isEmpty()) {
			model.addAttribute("all", 0);
			model.addAttribute("center", centerDao.centerNameDao(masterCenterId));
		} else {
			model.addAttribute("list", lessonsDao.surveyLessonsListDao(cri, masterCenterId));
			model.addAttribute("center", centerDao.centerNameDao(masterCenterId));
		}

		return "survey_list";
	}

	@GetMapping("/survey_form")
	public String survey_form(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER2, required = false) String check,
			Model model, int LESSON_CENTER_ID) {
		if (check == null) {
			return "redirect:/survey-login";
		}

		masterLessonCenterId = LESSON_CENTER_ID;
		model.addAttribute("ttl", lessonsDao.TtlDao(LESSON_CENTER_ID));

		return "survey_form";
	}

	@PostMapping("/survey-post")
	public String surveyPost(@RequestBody String result) {

		int sum;
		int ee;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);

		sum = element.getAsJsonObject().get("sum").getAsInt();
		ee = element.getAsJsonObject().get("answer06").getAsInt();

		lessonsDao.surveyUpdateDao(sum, masterLessonCenterId);

		return "redirect:/survey_list";
	}

	@GetMapping("/search")
	public String search(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model, String keyword) {

		if (check == null) {
			return "redirect:/login";
		}

		int pageStart = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", lessonsDao.searchDao(keyword, cri, pageStart, perPageNum));

		Paging pageMaker = new Paging();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(lessonsDao.getSearchCount(keyword));

		model.addAttribute("pageMaker", pageMaker);
		model.addAttribute("keyword", keyword);
		if (lessonsDao.getSearchCount(keyword) == 0) {
			model.addAttribute("check", 0);
		} else {
			model.addAttribute("check", 1);
		}

		return "admin01_search";
	}

	@PostMapping("/register")
	public String register(@RequestBody String result) {

		LocalDateTime now = LocalDateTime.now();

		String LESSON_TTL = "";
		String BGNG_DT = "";
		String END_DT = "";

		String LESSON_TM = "";
		String ROOM_ID = "";
		String INSTR_NM = "";
		String LESSON_TYPE = "";
		String LESSON_DESC = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		LESSON_TTL = element.getAsJsonObject().get("LESSON_TTL").getAsString();
		BGNG_DT = element.getAsJsonObject().get("BGNG_DT").getAsString();

		END_DT = element.getAsJsonObject().get("END_DT").getAsString();
		LESSON_TM = element.getAsJsonObject().get("LESSON_TM").getAsString();
		ROOM_ID = element.getAsJsonObject().get("ROOM_ID").getAsString();
		INSTR_NM = element.getAsJsonObject().get("INSTR_NM").getAsString();
		LESSON_TYPE = element.getAsJsonObject().get("LESSON_TYPE").getAsString();
		LESSON_DESC = element.getAsJsonObject().get("LESSON_DESC").getAsString();

		lessonsDao.registerDao(LESSON_TTL, BGNG_DT, END_DT, LESSON_TM, ROOM_ID, INSTR_NM, LESSON_TYPE, LESSON_DESC);

		return "redirect:/admin";
	}

	@PostMapping("/update")
	public @ResponseBody String update(@RequestBody String result) {
		LocalDateTime now = LocalDateTime.now();

		String LESSON_TTL = "";
		String BGNG_DT = "";
		String END_DT = "";

		String LESSON_TM = "";
		String ROOM_ID = "";
		String INSTR_NM = "";
		String LESSON_TYPE = "";
		String LESSON_DESC = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		LESSON_TTL = element.getAsJsonObject().get("LESSON_TTL").getAsString();
		BGNG_DT = element.getAsJsonObject().get("BGNG_DT").getAsString();
		END_DT = element.getAsJsonObject().get("END_DT").getAsString();
		LESSON_TM = element.getAsJsonObject().get("LESSON_TM").getAsString();
		ROOM_ID = element.getAsJsonObject().get("ROOM_ID").getAsString();
		INSTR_NM = element.getAsJsonObject().get("INSTR_NM").getAsString();
		LESSON_TYPE = element.getAsJsonObject().get("LESSON_TYPE").getAsString();
		LESSON_DESC = element.getAsJsonObject().get("LESSON_DESC").getAsString();

		lessonsDao.updateDao(masterId, LESSON_TTL, BGNG_DT, END_DT, LESSON_TM, ROOM_ID, INSTR_NM, LESSON_TYPE,
				LESSON_DESC, now);

		return "redirect:/view";
	}

	@RequestMapping("/popup")
	public String popup(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			String date, Model model) {
		if (check == null) {
			return "redirect:/login";
		}
		model.addAttribute("ar01_10", arDao.getCount01_10(date));
		model.addAttribute("ar01_11", arDao.getCount01_11(date));
		model.addAttribute("ar01_12", arDao.getCount01_12(date));
		model.addAttribute("ar01_13", arDao.getCount01_13(date));
		model.addAttribute("ar01_14", arDao.getCount01_14(date));
		model.addAttribute("ar01_15", arDao.getCount01_15(date));
		model.addAttribute("ar01_16", arDao.getCount01_16(date));
		model.addAttribute("ar01_17", arDao.getCount01_17(date));

		model.addAttribute("ar02_10", arDao.getCount02_10(date));
		model.addAttribute("ar02_11", arDao.getCount02_11(date));
		model.addAttribute("ar02_12", arDao.getCount02_12(date));
		model.addAttribute("ar02_13", arDao.getCount02_13(date));
		model.addAttribute("ar02_14", arDao.getCount02_14(date));
		model.addAttribute("ar02_15", arDao.getCount02_15(date));
		model.addAttribute("ar02_16", arDao.getCount02_16(date));
		model.addAttribute("ar02_17", arDao.getCount02_17(date));

		model.addAttribute("ar11_10", arDao.getCount11_10(date));
		model.addAttribute("ar11_11", arDao.getCount11_11(date));
		model.addAttribute("ar11_12", arDao.getCount11_12(date));
		model.addAttribute("ar11_13", arDao.getCount11_13(date));
		model.addAttribute("ar11_14", arDao.getCount11_14(date));
		model.addAttribute("ar11_15", arDao.getCount11_15(date));
		model.addAttribute("ar11_16", arDao.getCount11_16(date));
		model.addAttribute("ar11_17", arDao.getCount11_17(date));

		model.addAttribute("ar12_10", arDao.getCount12_10(date));
		model.addAttribute("ar12_11", arDao.getCount12_11(date));
		model.addAttribute("ar12_12", arDao.getCount12_12(date));
		model.addAttribute("ar12_13", arDao.getCount12_13(date));
		model.addAttribute("ar12_14", arDao.getCount12_14(date));
		model.addAttribute("ar12_15", arDao.getCount12_15(date));
		model.addAttribute("ar12_16", arDao.getCount12_16(date));
		model.addAttribute("ar12_17", arDao.getCount12_17(date));

		model.addAttribute("arsum_10", arDao.getCount01_10(date) + arDao.getCount02_10(date) + arDao.getCount11_10(date)
				+ arDao.getCount12_10(date));
		model.addAttribute("arsum_11", arDao.getCount01_11(date) + arDao.getCount02_11(date) + arDao.getCount11_11(date)
				+ arDao.getCount12_11(date));
		model.addAttribute("arsum_12", arDao.getCount01_12(date) + arDao.getCount02_12(date) + arDao.getCount11_12(date)
				+ arDao.getCount12_12(date));
		model.addAttribute("arsum_13", arDao.getCount01_13(date) + arDao.getCount02_13(date) + arDao.getCount11_13(date)
				+ arDao.getCount12_13(date));
		model.addAttribute("arsum_14", arDao.getCount01_14(date) + arDao.getCount02_14(date) + arDao.getCount11_14(date)
				+ arDao.getCount12_14(date));
		model.addAttribute("arsum_15", arDao.getCount01_15(date) + arDao.getCount02_15(date) + arDao.getCount11_15(date)
				+ arDao.getCount12_15(date));
		model.addAttribute("arsum_16", arDao.getCount01_16(date) + arDao.getCount02_16(date) + arDao.getCount11_16(date)
				+ arDao.getCount12_16(date));
		model.addAttribute("arsum_17", arDao.getCount01_17(date) + arDao.getCount02_17(date) + arDao.getCount11_17(date)
				+ arDao.getCount12_17(date));

		model.addAttribute("time", date);

		return "popup";

	}

	@RequestMapping("/popup-center")
	public String popup_center(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			String date, Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		return "popup_center";

	}

	@RequestMapping("/view")
	public String view(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			int LESSON_ID, Model model) {
		if (check == null) {
			return "redirect:/login";
		}
		model.addAttribute("view", lessonsDao.lessonsDao(LESSON_ID));
		masterId = LESSON_ID;
		if (lessonsDao.totalCountDao(LESSON_ID) == null || lessonsDao.centerCountDao(LESSON_ID) == null) {
			model.addAttribute("total_count", 0);
			model.addAttribute("center_count", 0);
		} else {
			model.addAttribute("total_count", lessonsDao.totalCountDao(LESSON_ID));
			model.addAttribute("center_count", lessonsDao.centerCountDao(LESSON_ID));

			if (lessonsDao.surveyDao(LESSON_ID) == null) {
				model.addAttribute("DGSTFN_SCORE", "아직 설문조사를 한 기관이 없습니다.");
			} else {
				model.addAttribute("DGSTFN_SCORE", lessonsDao.surveyDao(LESSON_ID) + "점");
			}

		}

		model.addAttribute("list", lessonsDao.centerLessonsListDao(LESSON_ID));
		// model.addAttribute("list", lessonsDao.centerTTListDao());
		return "admin01_view";
	}

	@RequestMapping("/lect_result")
	public @ResponseBody Map<String, String> admin01_view(Model model, @RequestBody String result, Exception e) {
		Map<String, String> map = new HashMap<>();
		map.put("rt_code", map.getOrDefault("rt_code", "0"));
		map.put("message", map.getOrDefault("message", "success"));
		LocalDate now = LocalDate.now();
		int LESSON_ID;
		String CENTER_ID = "";
		int total_count;
		int NOPE;
		JsonArray center_count;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);

		LESSON_ID = element.getAsJsonObject().get("lecture_id").getAsInt();
		total_count = element.getAsJsonObject().get("total_count").getAsInt();
		// center_count =
		// element.getAsJsonObject().get("center_count").getAsJsonArray();

		JsonObject jsonObj = (JsonObject) parser.parse(result);

		JsonArray memberArray = (JsonArray) jsonObj.get("center_count");

		for (int i = 0; i < memberArray.size(); i++) {
			JsonObject object = (JsonObject) memberArray.get(i);
			String a = object.get("center_id").getAsString();
			int b = object.get("count").getAsInt();

			if (centerDao.checkDao2(LESSON_ID, "050400" + a) == null) {
				centerDao.centerLessonsInsert(LESSON_ID, "050400" + a, b);
			}
		}
		if (LESSON_ID != 0) {
			return map;
		} else {
			return handleException(e);
		}

	}

	@GetMapping("/lect_list")
	public @ResponseBody List<lect_listDto> lect_list() {
		lessonsDao.lect_listDao();

		return lessonsDao.lect_listDao();
	}

	@GetMapping("/ar")
	public String test(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		// model.addAttribute("co", monthSum());

		return "admin02";
	}

	@GetMapping("/ar-list")
	public @ResponseBody String arList() {

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		String tt;
		tt = gson.toJson(arDao.arAllList());

		return tt;
	}

	@GetMapping("/ardate")
	public String ardate(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		return "date";
	}

	@PostMapping("/chartPost")
	public @ResponseBody HashMap<Integer, Integer> chart(@RequestBody String result) {

		HashMap<Integer, Integer> map = new HashMap<>();

		masterResult = result;

		return map;
	}

	@RequestMapping("/graph")
	public String graph(Model model, String x, String start, String end, String typeDirect, String typeDirect1,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("x", x);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("typeDirect", typeDirect);
		model.addAttribute("typeDirect1", typeDirect1);

		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		model.addAttribute("year", year);
		return "admin05_search";
	}

	@RequestMapping("/chart-get-ar")
	public @ResponseBody HashMap<String, List<Integer>> chartGetAr() throws ParseException {

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		int start;
		int end;
		String month_start = "";
		String month_end = "";
		int base;
		int x;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(masterResult);

		x = element.getAsJsonObject().get("x").getAsInt();
		start = element.getAsJsonObject().get("start").getAsInt();
		end = element.getAsJsonObject().get("end").getAsInt();
		month_start = element.getAsJsonObject().get("typeDirect").getAsString();
		month_end = element.getAsJsonObject().get("typeDirect1").getAsString();
		int a = Integer.valueOf(month_start);
		int b = Integer.valueOf(month_end);

		String date_str = start + "-" + month_start;
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM");

		Date date = transFormat.parse(date_str);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);

		if (x == 0) {
			if (start == end) {
				list.add(start);
				list2.add(arDao.arStatisticsSum(Integer.toString(start)));
				dd.put("month", list);
				dd.put("point", list2);
			}

			// 여기서부터 해야함 년으로 받았을 때 같은 년도는 해결
			else {
				for (int i = start; i < end + 1; i++) {
					list.add(i);
					list2.add(arDao.arStatisticsSum(Integer.toString(i)));
				}
				dd.put("month", list);
				dd.put("point", list2);

			}

		} // if 년일때

		if (x == 1) {

			if (start == end) {
				base = b - a;
				for (int i = 0; i < base + 1; i++) {

					list.add(a + i);
					if (i == 0) {
						cal1.add(Calendar.MONTH, 0);
						String aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arStatisticsSum(aa));
					} else {
						cal1.add(Calendar.MONTH, 1);
						String aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arStatisticsSum(aa));
					}

					dd.put("month", list);
					dd.put("point", list2);

					// map.put(a+i,arDao.arStatisticsSum(start+"-"+month_start));
				}
			}

			else {
				base = (b + 12) - a;
				cal1.add(Calendar.MONTH, 0);
				String aa = transFormat.format(cal1.getTime());

				list2.add(arDao.arStatisticsSum(aa));

				for (int i = 0; i < base + 1; i++) {
					if (a > 12) {
						a = 1;
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arStatisticsSum(aa));
						a++;
					} else {
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arStatisticsSum(aa));
						a++;
					}

				}
				dd.put("month", list);
				dd.put("point", list2);

				// base = 12+month_end;

			}
		} // x가 1일때 if

		return dd;
	}

	@RequestMapping("/chart-get-center")
	public @ResponseBody HashMap<String, List<Integer>> chartGetCenter() throws ParseException {

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		int start;
		int end;
		int x;
		String month_start = "";
		String month_end = "";
		int base;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(masterResult);

		x = element.getAsJsonObject().get("x").getAsInt();
		start = element.getAsJsonObject().get("start").getAsInt();
		end = element.getAsJsonObject().get("end").getAsInt();
		month_start = element.getAsJsonObject().get("typeDirect").getAsString();
		month_end = element.getAsJsonObject().get("typeDirect1").getAsString();
		int a = Integer.valueOf(month_start);
		int b = Integer.valueOf(month_end);
		String date_str = start + "-" + month_start;
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM");

		Date date = transFormat.parse(date_str);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		if (x == 0) {
			if (start == end) {
				list.add(start);
				list2.add(centerDao.centerStatisticsSum(Integer.toString(start)));
				dd.put("month", list);
				dd.put("point", list2);
			}

			else {
				for (int i = start; i < end + 1; i++) {
					list.add(i);
					list2.add(centerDao.centerStatisticsSum(Integer.toString(i)));
				}
				dd.put("month", list);
				dd.put("point", list2);

			}

		} // x가 0일때

		if (x == 1) {

			if (start == end) {
				base = b - a;
				for (int i = 0; i < base + 1; i++) {

					list.add(a + i);
					if (i == 0) {
						cal1.add(Calendar.MONTH, 0);
						String aa = transFormat.format(cal1.getTime());

						list2.add(centerDao.centerStatisticsSum(aa));
					} else {
						cal1.add(Calendar.MONTH, 1);
						String aa = transFormat.format(cal1.getTime());

						list2.add(centerDao.centerStatisticsSum(aa));
					}

					dd.put("month", list);
					dd.put("point", list2);

				}
			} else {

				base = (b + 12) - a;

				cal1.add(Calendar.MONTH, 0);
				String aa = transFormat.format(cal1.getTime());

				list2.add(centerDao.centerStatisticsSum(aa));
				for (int i = 0; i < base + 1; i++) {
					if (a > 12) {
						a = 1;
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(centerDao.centerStatisticsSum(aa));
						a++;
					} else {
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(centerDao.centerStatisticsSum(aa));
						a++;
					}

				}
				dd.put("month", list);
				dd.put("point", list2);

			}

		} // x가 월 일 때

		return dd;
	}

	@RequestMapping("/chart-get-ar-use")
	public @ResponseBody HashMap<String, List<Integer>> chartGetArUse() throws ParseException {

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		int start;
		int end;
		String month_start = "";
		String month_end = "";
		int base;
		int x;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(masterResult);

		x = element.getAsJsonObject().get("x").getAsInt();
		start = element.getAsJsonObject().get("start").getAsInt();
		end = element.getAsJsonObject().get("end").getAsInt();
		month_start = element.getAsJsonObject().get("typeDirect").getAsString();
		month_end = element.getAsJsonObject().get("typeDirect1").getAsString();
		int a = Integer.valueOf(month_start);
		int b = Integer.valueOf(month_end);

		String date_str = start + "-" + month_start;
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM");

		Date date = transFormat.parse(date_str);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);

		if (x == 0) {
			if (start == end) {
				list.add(start);
				list2.add(arDao.arUseStatisticsSum(56 * 21 * 12, Integer.toString(start)));
				dd.put("month", list);
				dd.put("point", list2);
			}

			// 여기서부터 해야함 년으로 받았을 때 같은 년도는 해결
			else {
				for (int i = start; i < end + 1; i++) {
					list.add(i);
					list2.add(arDao.arUseStatisticsSum(56 * 21 * 12, Integer.toString(i)));
				}
				dd.put("month", list);
				dd.put("point", list2);

			}

		} // if 년일때

		if (x == 1) {

			if (start == end) {
				base = b - a;
				for (int i = 0; i < base + 1; i++) {

					list.add(a + i);
					if (i == 0) {
						cal1.add(Calendar.MONTH, 0);
						String aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arUseStatisticsSum(56 * 21, aa));
					} else {
						cal1.add(Calendar.MONTH, 1);
						String aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arUseStatisticsSum(56 * 21, aa));
					}

					dd.put("month", list);
					dd.put("point", list2);

					// map.put(a+i,arDao.arStatisticsSum(start+"-"+month_start));
				}
			} else {

				base = (b + 12) - a;

				cal1.add(Calendar.MONTH, 0);
				String aa = transFormat.format(cal1.getTime());

				list2.add(arDao.arUseStatisticsSum(56 * 21, aa));

				for (int i = 0; i < base + 1; i++) {
					if (a > 12) {
						a = 1;
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arUseStatisticsSum(56 * 21, aa));
						a++;
					} else {
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(arDao.arUseStatisticsSum(56 * 21, aa));
						a++;
					}

				}
				dd.put("month", list);
				dd.put("point", list2);

			}
		} // x가 1일때 if

		return dd;
	}

	@RequestMapping("/chart-get-center-use")
	public @ResponseBody HashMap<String, List<Integer>> chartGetCenterUse() throws ParseException {

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		int start;
		int end;
		String month_start = "";
		String month_end = "";
		int base;
		int x;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(masterResult);

		x = element.getAsJsonObject().get("x").getAsInt();
		start = element.getAsJsonObject().get("start").getAsInt();
		end = element.getAsJsonObject().get("end").getAsInt();
		month_start = element.getAsJsonObject().get("typeDirect").getAsString();
		month_end = element.getAsJsonObject().get("typeDirect1").getAsString();
		int a = Integer.valueOf(month_start);
		int b = Integer.valueOf(month_end);
		String date_str = start + "-" + month_start;
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM");

		Date date = transFormat.parse(date_str);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		if (x == 0) {
			if (start == end) {
				list.add(start);

				list2.add(centerDao.centerUseStatisticsSum(Integer.toString(start)));
				dd.put("month", list);
				dd.put("point", list2);
			}

			// 여기서부터 해야함 년으로 받았을 때 같은 년도는 해결
			else {
				for (int i = start; i < end + 1; i++) {
					list.add(i);

					list2.add(centerDao.centerUseStatisticsSum(Integer.toString(i)));
				}
				dd.put("month", list);
				dd.put("point", list2);

			}

		}

		if (x == 1) {
			if (start == end) {
				base = b - a;
				for (int i = 0; i < base + 1; i++) {

					list.add(a + i);
					if (i == 0) {
						cal1.add(Calendar.MONTH, 0);
						String aa = transFormat.format(cal1.getTime());
						if (centerDao.centerUseStatisticsSum(aa) == null) {
							list2.add(0);
						} else {

							list2.add(centerDao.centerUseStatisticsSum(aa));
						}
					} else {
						cal1.add(Calendar.MONTH, 1);
						String aa = transFormat.format(cal1.getTime());
						if (centerDao.centerUseStatisticsSum(aa) == null) {
							list2.add(0);
						} else {

							list2.add(centerDao.centerUseStatisticsSum(aa));

						}
					}

					dd.put("month", list);
					dd.put("point", list2);

				}
			} else {

				base = (b + 12) - a;

				cal1.add(Calendar.MONTH, 0);
				String aa = transFormat.format(cal1.getTime());

				list2.add(centerDao.centerUseStatisticsSum(aa));
				for (int i = 0; i < base + 1; i++) {
					if (a > 12) {
						a = 1;
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(centerDao.centerUseStatisticsSum(aa));
						a++;
					} else {
						list.add(a);
						cal1.add(Calendar.MONTH, 1);
						aa = transFormat.format(cal1.getTime());

						list2.add(centerDao.centerUseStatisticsSum(aa));
						a++;
					}

				}
				dd.put("month", list);
				dd.put("point", list2);

				// base = 12+month_end;
			}

		}

		return dd;
	}

	@RequestMapping("/chart-get-li")
	public @ResponseBody HashMap<String, List<Integer>> chartGetLi() throws ParseException {

		HashMap<String, List<Integer>> dd = new HashMap<>();
		HashMap<String, List<Integer>> dd2 = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		int start;
		int end;
		int x;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(masterResult);
		x = element.getAsJsonObject().get("x").getAsInt();
		start = element.getAsJsonObject().get("start").getAsInt();
		end = element.getAsJsonObject().get("end").getAsInt();

		// int increase= Integer.parseInt(year)-1;
		// String in= Integer.toString(increase);
		// double
		// a=((libraryDao.getLibraryPeopleDao(year)-libraryDao.getLibraryPeopleDao(in))/(double)libraryDao.getLibraryPeopleDao(year))*100;
		// list2.add((int) Math.round(a));
		double a;
		if (start == end) {
			list.add(start);
			int increase = start - 1;
			String in = Integer.toString(increase);
			if (libraryDao.getLibraryPeopleDao(Integer.toString(start)) != null
					&& libraryDao.getLibraryPeopleDao(in) != null) {

				a = ((libraryDao.getLibraryPeopleDao(Integer.toString(start)) - libraryDao.getLibraryPeopleDao(in))
						/ (double) libraryDao.getLibraryPeopleDao(in)) * 100;
			} else {
				a = 0;
			}
			list2.add((int) Math.round(a));
			dd.put("month", list);
			dd.put("point", list2);
		}

		// 여기서부터 해야함 년으로 받았을 때 같은 년도는 해결
		else {
			for (int i = start; i < end + 1; i++) {
				list.add(i);
				int increase = i - 1;
				String in = Integer.toString(increase);
				if (libraryDao.getLibraryPeopleDao(Integer.toString(i)) != null
						&& libraryDao.getLibraryPeopleDao(in) != null) {
					a = ((libraryDao.getLibraryPeopleDao(Integer.toString(i)) - libraryDao.getLibraryPeopleDao(in))
							/ (double) libraryDao.getLibraryPeopleDao(in)) * 100;
				} else {
					a = 0;
				}
				list2.add((int) Math.round(a));

			}
			dd.put("month", list);
			dd.put("point", list2);
		}
		if (x == 1) {
			return dd2;
		}

		return dd;
	}

	@RequestMapping("/monthAll")
	public @ResponseBody ArrayList<Integer> test12(@RequestBody String test) {
		ArrayList<Integer> n = new ArrayList<>();

		LocalDate now = LocalDate.now();

		String year = "";
		String month = "";
		String day;
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(test);

		year = element.getAsJsonObject().get("year").getAsString();
		month = element.getAsJsonObject().get("month").getAsString();
		int a = Integer.parseInt(month);
		if (a == 1 || a == 2 || a == 3 || a == 4 || a == 5 || a == 6 || a == 7 || a == 8 || a == 9) {
			month = "0" + month;

		}
		day = year + "-" + month;

		int m1 = arDao.getCount01_10("2022-10-20") + arDao.getCount02_10("2022-10-20")
				+ arDao.getCount11_10("2022-10-20") + arDao.getCount12_10("2022-10-20")
				+ arDao.getCount01_11("2022-10-20") + arDao.getCount02_11("2022-10-20")
				+ arDao.getCount11_11("2022-10-20") + arDao.getCount12_11("2022-10-20")
				+ arDao.getCount01_12("2022-10-20") + arDao.getCount02_12("2022-10-20")
				+ arDao.getCount11_12("2022-10-20") + arDao.getCount12_12("2022-10-20")
				+ arDao.getCount01_13("2022-10-20") + arDao.getCount02_13("2022-10-20")
				+ arDao.getCount11_13("2022-10-20") + arDao.getCount12_13("2022-10-20")
				+ arDao.getCount01_14("2022-10-20") + arDao.getCount02_14("2022-10-20")
				+ arDao.getCount11_14("2022-10-20") + arDao.getCount12_14("2022-10-20")
				+ arDao.getCount01_15("2022-10-20") + arDao.getCount02_15("2022-10-20")
				+ arDao.getCount11_15("2022-10-20") + arDao.getCount12_15("2022-10-20")
				+ arDao.getCount01_16("2022-10-20") + arDao.getCount02_16("2022-10-20")
				+ arDao.getCount11_16("2022-10-20") + arDao.getCount12_16("2022-10-20")
				+ arDao.getCount01_17("2022-10-20") + arDao.getCount02_17("2022-10-20")
				+ arDao.getCount11_17("2022-10-20") + arDao.getCount12_17("2022-10-20");

		int n1 = arDao.getCountday01(day + "-01");
		int n2 = arDao.getCountday02(day + "-02");
		int n3 = arDao.getCountday03(day + "-03");
		int n4 = arDao.getCountday04(day + "-04");
		int n5 = arDao.getCountday05(day + "-05");
		int n6 = arDao.getCountday06(day + "-06");
		int n7 = arDao.getCountday07(day + "-07");
		int n8 = arDao.getCountday08(day + "-08");
		int n9 = arDao.getCountday09(day + "-09");
		int n10 = arDao.getCountday10(day + "-10");
		int n11 = arDao.getCountday11(day + "-11");
		int n12 = arDao.getCountday12(day + "-12");
		int n13 = arDao.getCountday13(day + "-13");
		int n14 = arDao.getCountday14(day + "-14");
		int n15 = arDao.getCountday15(day + "-15");
		int n16 = arDao.getCountday16(day + "-16");
		int n17 = arDao.getCountday17(day + "-17");
		int n18 = arDao.getCountday18(day + "-18");
		int n19 = arDao.getCountday19(day + "-19");
		int n20 = arDao.getCountday20(day + "-20");
		int n21 = arDao.getCountday21(day + "-21");
		int n22 = arDao.getCountday22(day + "-22");
		int n23 = arDao.getCountday23(day + "-23");
		int n24 = arDao.getCountday24(day + "-24");
		int n25 = arDao.getCountday25(day + "-25");
		int n26 = arDao.getCountday26(day + "-26");
		int n27 = arDao.getCountday27(day + "-27");
		int n28 = arDao.getCountday28(day + "-28");
		n.add(n1);
		n.add(n2);
		n.add(n3);
		n.add(n4);
		n.add(n5);
		n.add(n6);
		n.add(n7);
		n.add(n8);
		n.add(n9);
		n.add(n10);
		n.add(n11);
		n.add(n12);
		n.add(n13);
		n.add(n14);
		n.add(n15);
		n.add(n16);
		n.add(n17);
		n.add(n18);
		n.add(n19);
		n.add(n20);
		n.add(n21);
		n.add(n22);
		n.add(n23);
		n.add(n24);
		n.add(n25);
		n.add(n26);
		n.add(n27);
		n.add(n28);

		if (a == 4 || a == 6 || a == 9 || a == 11 || a == 1 || a == 3 || a == 5 || a == 7 || a == 8 || a == 10
				|| a == 12) {
			int n29 = arDao.getCountday29(day + "-29");
			int n30 = arDao.getCountday30(day + "-30");
			n.add(n29);
			n.add(n30);

		}

		if (a == 1 || a == 3 || a == 5 || a == 7 || a == 8 || a == 10 || a == 12) {
			int n31 = arDao.getCountday31(day + "-31");
			n.add(n31);
		}

		return n;
	}

	@RequestMapping("/month-sum")
	public @ResponseBody int monthPeople(@RequestBody String test) {

		int sum = 0;
		String year = "";
		String month = "";
		String day;
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(test);

		year = element.getAsJsonObject().get("year").getAsString();
		month = element.getAsJsonObject().get("month").getAsString();
		int a = Integer.parseInt(month);
		if (a == 1 || a == 2 || a == 3 || a == 4 || a == 5 || a == 6 || a == 7 || a == 8 || a == 9) {
			month = "0" + month;

		}
		day = year + "-" + month;

		sum = arDao.getCountday01(day + "-01") + arDao.getCountday02(day + "-02") + arDao.getCountday03(day + "-03")
				+ arDao.getCountday04(day + "-04") + arDao.getCountday05(day + "-05") + arDao.getCountday06(day + "-06")
				+ arDao.getCountday07(day + "-07") + arDao.getCountday08(day + "-08") + arDao.getCountday09(day + "-09")
				+ arDao.getCountday10(day + "-10") + arDao.getCountday11(day + "-11") + arDao.getCountday12(day + "-12")
				+ arDao.getCountday13(day + "-13") + arDao.getCountday14(day + "-14") + arDao.getCountday15(day + "-15")
				+ arDao.getCountday16(day + "-16") + arDao.getCountday17(day + "-17") + arDao.getCountday18(day + "-18")
				+ arDao.getCountday19(day + "-19") + arDao.getCountday20(day + "-20") + arDao.getCountday21(day + "-21")
				+ arDao.getCountday22(day + "-22") + arDao.getCountday23(day + "-23") + arDao.getCountday24(day + "-24")
				+ arDao.getCountday25(day + "-25") + arDao.getCountday26(day + "-26") + arDao.getCountday27(day + "-27")
				+ arDao.getCountday28(day + "-28");

		if (a == 1 || a == 3 || a == 4 || a == 5 || a == 6 || a == 7 || a == 8 || a == 9 || a == 10 || a == 11
				|| a == 12) {

			sum += arDao.getCountday29(day + "-29");
			sum += arDao.getCountday30(day + "-30");
		}

		if (a == 1 || a == 3 || a == 5 || a == 7 || a == 8 || a == 10 || a == 12) {
			sum += arDao.getCountday31(day + "-31");
		}

		// arDao.getCountday29(day + "-29") + arDao.getCountday30(day + "-30")

		return sum;
	}

	@PostMapping("/ar_user_log")
	public @ResponseBody Map<String, String> mistake(@RequestBody String result) {
		Map<String, String> map = new HashMap<>();
		map.put("rt_code", map.getOrDefault("rt_code", "0"));
		map.put("message", map.getOrDefault("message", "success"));

		LocalDate now = LocalDate.now();
		String USE_DT = "";
		String AR_CONTENT_TYPE = "";
		String AR_USER_CATEGORY = "";
		Integer DGSTFN_SCORE;
		String GENDER = "";
		String ADDRESS = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		USE_DT = element.getAsJsonObject().get("datetime").getAsString();
		AR_CONTENT_TYPE = element.getAsJsonObject().get("content_type").getAsString();
		AR_USER_CATEGORY = element.getAsJsonObject().get("user_category").getAsString();
		GENDER = element.getAsJsonObject().get("gender").getAsString();
		ADDRESS = element.getAsJsonObject().get("address").getAsString();

		if (element.getAsJsonObject().get("dgstfn_score").isJsonNull()) {

			DGSTFN_SCORE = null;
		} else {
			DGSTFN_SCORE = element.getAsJsonObject().get("dgstfn_score").getAsInt();

		}

		arDao.arRegisterDao(USE_DT, AR_CONTENT_TYPE, AR_USER_CATEGORY, DGSTFN_SCORE, now, GENDER, ADDRESS);

		return map;
	}

	@GetMapping("/center")
	public String center(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model, PageDTO pageDTO) {
		if (check == null) {
			return "redirect:/login";
		}

		pageDTO.setTotalCount(centerDao.getCount());
		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		model.addAttribute("center", centerDao.centerListDao(cri, pageDTO, start, perPageNum));

		Paging pageMaker = new Paging();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(centerDao.getCount());

		model.addAttribute("pageMaker", pageMaker);

		if (centerDao.getCount() == 0) {
			model.addAttribute("check", 0);
		} else {
			model.addAttribute("check", 1);
		}

		return "admin03_list";
	}

	@RequestMapping("/center-register")
	public @ResponseBody int centerRegister(@RequestBody String result) {
		int a = 0;
		LocalDateTime now = LocalDateTime.now();

		String CENTER_TTL = "";
		String CENTER_ID = "";
		String CENTER_UID = "";
		String CENTER_PSWD = "";
		int CENTER_MAX_NOPE;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		CENTER_TTL = element.getAsJsonObject().get("CENTER_TTL").getAsString();
		CENTER_ID = element.getAsJsonObject().get("CENTER_ID").getAsString();
		CENTER_UID = element.getAsJsonObject().get("CENTER_UID").getAsString();
		CENTER_PSWD = element.getAsJsonObject().get("CENTER_PSWD").getAsString();
		CENTER_MAX_NOPE = element.getAsJsonObject().get("CENTER_MAX_NOPE").getAsInt();

		if (centerDao.checkIdDao(CENTER_ID) == null) {
			centerDao.registerDao(CENTER_TTL, CENTER_ID, CENTER_UID, CENTER_PSWD, CENTER_MAX_NOPE);
			a = 0;
		} else {
			if (centerDao.checkDao(CENTER_ID) == null) {
				a = 1;
			} else {

				centerDao.updateDao2(CENTER_TTL, CENTER_ID, CENTER_UID, CENTER_PSWD, CENTER_MAX_NOPE, now, null);

				a = 0;
			}
		}

		return a;
	}

	@GetMapping("/center-update-modal")
	public String centerupdatemodal(String CENTER_ID, Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("view", centerDao.centerDao(CENTER_ID));

		return "update";
	}

	@PostMapping("/center-update")
	public String centerupdate(@RequestBody String result) {
		LocalDateTime now = LocalDateTime.now();

		String CENTER_TTL = "";
		String CENTER_ID = "";
		String CENTER_UID = "";
		String CENTER_PSWD = "";
		int CENTER_MAX_NOPE;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		CENTER_TTL = element.getAsJsonObject().get("CENTER_TTL").getAsString();
		CENTER_ID = element.getAsJsonObject().get("CENTER_ID").getAsString();
		CENTER_UID = element.getAsJsonObject().get("CENTER_UID").getAsString();
		CENTER_PSWD = element.getAsJsonObject().get("CENTER_PSWD").getAsString();
		CENTER_MAX_NOPE = element.getAsJsonObject().get("CENTER_MAX_NOPE").getAsInt();

		centerDao.updateDao(CENTER_TTL, CENTER_ID, CENTER_UID, CENTER_PSWD, CENTER_MAX_NOPE, now);

		return "redirect:/center";
	}

	@GetMapping("/center-search")
	public String centersearch(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			String CENTER_ID, Model model, @ModelAttribute("cri") Criteria cri, String keyword, PageDTO pageDTO) {

		if (check == null) {
			return "redirect:/login";

		}
		pageDTO.setTotalCount(centerDao.getSearchCount(keyword));

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		model.addAttribute("center", centerDao.searchDao(keyword, cri, pageDTO, start, perPageNum));

		Paging pageMaker = new Paging();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(centerDao.getSearchCount(keyword));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("keyword", keyword);

		if (centerDao.getSearchCount(keyword) == 0) {
			model.addAttribute("check", 0);
		} else {
			model.addAttribute("check", 1);
		}

		return "admin03_search";
	}

	@PostMapping("/center-delete")
	public String centerDelete(@RequestBody String CENTER_ID) {
		LocalDateTime now = LocalDateTime.now();
		centerDao.deleteDao(now, CENTER_ID);

		return "redirect:/center";

	}

	@GetMapping("/satisfaction")
	public String satisfaction(Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		model.addAttribute("year", year);
		if (arDao.getCount01(year) != 0) {
			model.addAttribute("fish", arDao.getCount01(year));
			model.addAttribute("fishsum", arDao.sum01(year));
			model.addAttribute("fishav", arDao.sum01(year) / arDao.getCount01(year));
		} else {

			model.addAttribute("fish", 0);
			model.addAttribute("fishsum", 0);
			model.addAttribute("fishav", 0);
		}
		if (arDao.getCount02(year) != 0) {
			model.addAttribute("yg", arDao.getCount02(year));
			model.addAttribute("ygsum", arDao.sum02(year));
			model.addAttribute("ygav", arDao.sum02(year) / arDao.getCount02(year));
		} else {

			model.addAttribute("yg", 0);
			model.addAttribute("ygsum", 0);
			model.addAttribute("ygav", 0);
		}
		if (arDao.getCount11(year) != 0) {
			model.addAttribute("arfish", arDao.getCount11(year));
			model.addAttribute("arfishsum", arDao.sum11(year));
			model.addAttribute("arfishav", arDao.sum11(year) / arDao.getCount11(year));
		} else {

			model.addAttribute("arfish", 0);
			model.addAttribute("arfishsum", 0);
			model.addAttribute("arfishav", 0);
		}
		if (arDao.getCount12(year) != 0) {
			model.addAttribute("aryg", arDao.getCount12(year));
			model.addAttribute("arygsum", arDao.sum12(year));
			model.addAttribute("arygav", arDao.sum12(year) / arDao.getCount12(year));
		} else {

			model.addAttribute("aryg", 0);
			model.addAttribute("arygsum", 0);
			model.addAttribute("arygav", 0);
		}

		if (lessonsDao.getCountAll(year) != 0) {
			model.addAttribute("all", lessonsDao.getCountAll(year));
			model.addAttribute("sumall", lessonsDao.sumAll(year));
			float a = lessonsDao.sumAll(year) / lessonsDao.getCountAll(year);
			model.addAttribute("avall", a);
		} else {

			model.addAttribute("all", 0);
			model.addAttribute("sumall", 0);
			model.addAttribute("avall", 0);
		}

		return "admin04";

	}

	@GetMapping("/dgs")
	public String dgs(String year, Model model, String mon, String year1,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("year", year1);
		model.addAttribute("mon", mon);

		if (arDao.getCount01(year) != 0) {
			model.addAttribute("fish", arDao.getCount01(year));
			model.addAttribute("fishsum", arDao.sum01(year));
			model.addAttribute("fishav", arDao.sum01(year) / arDao.getCount01(year));
		} else {

			model.addAttribute("fish", 0);
			model.addAttribute("fishsum", 0);
			model.addAttribute("fishav", 0);
		}
		if (arDao.getCount02(year) != 0) {
			model.addAttribute("yg", arDao.getCount02(year));
			model.addAttribute("ygsum", arDao.sum02(year));
			model.addAttribute("ygav", arDao.sum02(year) / arDao.getCount02(year));
		} else {

			model.addAttribute("yg", 0);
			model.addAttribute("ygsum", 0);
			model.addAttribute("ygav", 0);
		}
		if (arDao.getCount11(year) != 0) {
			model.addAttribute("arfish", arDao.getCount11(year));
			model.addAttribute("arfishsum", arDao.sum11(year));
			model.addAttribute("arfishav", arDao.sum11(year) / arDao.getCount11(year));
		} else {

			model.addAttribute("arfish", 0);
			model.addAttribute("arfishsum", 0);
			model.addAttribute("arfishav", 0);
		}
		if (arDao.getCount12(year) != 0) {
			model.addAttribute("aryg", arDao.getCount12(year));
			model.addAttribute("arygsum", arDao.sum12(year));
			model.addAttribute("arygav", arDao.sum12(year) / arDao.getCount12(year));
		} else {

			model.addAttribute("aryg", 0);
			model.addAttribute("arygsum", 0);
			model.addAttribute("arygav", 0);
		}

		if (lessonsDao.getCountAll(year) != 0) {
			model.addAttribute("all", lessonsDao.getCountAll(year));
			model.addAttribute("sumall", lessonsDao.sumAll(year));
			float a = lessonsDao.sumAll(year) / lessonsDao.getCountAll(year);
			model.addAttribute("avall", a);
		} else {

			model.addAttribute("all", 0);
			model.addAttribute("sumall", 0);
			model.addAttribute("avall", 0);
		}

		return "admin04_view";

	}

	@PostMapping("/delete")
	public String delete() {
		LocalDateTime now = LocalDateTime.now();

		lessonsDao.deleteDao(now, masterId);

		return "redirect:/admin";
	}

	@GetMapping("/statistics")
	public String statistics(Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		model.addAttribute("year", year);
		return "admin05";

	}

	@RequestMapping("/chart-get-ar-2022")
	public @ResponseBody HashMap<String, List<Integer>> chartGetAr2022() throws ParseException {
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		list.add(Integer.parseInt(year));
		list2.add(arDao.arStatisticsSum(year));
		dd.put("month", list);
		dd.put("point", list2);

		return dd;
	}

	@RequestMapping("/chart-get-center-2022")
	public @ResponseBody HashMap<String, List<Integer>> chartGetCenter2022() throws ParseException {
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		list.add(Integer.parseInt(year));
		list2.add(centerDao.centerStatisticsSum(year));
		dd.put("month", list);
		dd.put("point", list2);

		return dd;
	}

	@RequestMapping("/chart-get-ar-use-2022")
	public @ResponseBody HashMap<String, List<Integer>> chartGetArUse2022() throws ParseException {
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		list.add(Integer.parseInt(year));
		list2.add(arDao.arUseStatisticsSum(56 * 21 * 12, year));

		dd.put("month", list);
		dd.put("point", list2);

		return dd;
	}

	@RequestMapping("/chart-get-center-use-2022")
	public @ResponseBody HashMap<String, List<Integer>> chartGetCenterUse2022() throws ParseException {
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		list.add(Integer.parseInt(year));

		list2.add(centerDao.centerUseStatisticsSum(year));

		dd.put("month", list);
		dd.put("point", list2);

		return dd;
	}

	@RequestMapping("/chart-get-li-2022")
	public @ResponseBody HashMap<String, List<Integer>> chartGetLi2022(Model model) throws ParseException {
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		int increase = Integer.parseInt(year) - 1;
		String in = Integer.toString(increase);
		double a;
		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		list.add(Integer.parseInt(year));

		if (libraryDao.getLibraryPeopleDao(year) != null && libraryDao.getLibraryPeopleDao(in) != null) {
			a = ((libraryDao.getLibraryPeopleDao(year) - libraryDao.getLibraryPeopleDao(in))
					/ (double) libraryDao.getLibraryPeopleDao(in)) * 100;
		} else {
			a = 0;
		}

		list2.add((int) Math.round(a));
		model.addAttribute("year", year);
		dd.put("month", list);
		dd.put("point", list2);

		return dd;
	}

	@GetMapping("/goals")
	public String goals(Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		// userDao.goalDao(year);
		model.addAttribute("year", year);
		if (userDao.goalDao(year).isEmpty()) {
			model.addAttribute("view", null);
		} else {
			model.addAttribute("view", userDao.goalDao(year));
		}

		return "admin06";

	}

	@GetMapping("/goals-lookup")
	public String goals2(Model model, String year,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("year", year);

		if (userDao.goalDao(year).isEmpty()) {
			model.addAttribute("view", null);
		} else {
			model.addAttribute("view", userDao.goalDao(year));
		}

		return "admin06";

	}

	@RequestMapping("/goals-info")
	public @ResponseBody int goalsInfo(@RequestBody String year) {
		int dd = 1;
		if (userDao.goalDao(year).isEmpty()) {
			dd = 1;

		} else {

			dd = 2;
		}

		return dd;

	}

	@RequestMapping("/goal-register")
	public String goal_register(@RequestBody String result) {

		String a = "";
		String b = "";
		String c = "";
		String d = "";
		String e = "";
		int f;
		int year;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		a = element.getAsJsonObject().get("a").getAsString();
		b = element.getAsJsonObject().get("b").getAsString();
		c = element.getAsJsonObject().get("c").getAsString();
		d = element.getAsJsonObject().get("d").getAsString();
		e = element.getAsJsonObject().get("e").getAsString();
		f = element.getAsJsonObject().get("f").getAsInt();
		year = element.getAsJsonObject().get("year").getAsInt();

		userDao.goalRegisterDao(a, b, c, d, e, f, year);

		return "redirect:/goals";
	}

	@RequestMapping("/goal-update")
	public String goal_update(@RequestBody String result) {

		String a = "";
		String b = "";
		String c = "";
		String d = "";
		String e = "";
		int f;
		int year;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		a = element.getAsJsonObject().get("a").getAsString();
		b = element.getAsJsonObject().get("b").getAsString();
		c = element.getAsJsonObject().get("c").getAsString();
		d = element.getAsJsonObject().get("d").getAsString();
		e = element.getAsJsonObject().get("e").getAsString();
		f = element.getAsJsonObject().get("f").getAsInt();
		year = element.getAsJsonObject().get("year").getAsInt();
		userDao.goalUpdateDao(a, b, c, d, e, f, year);

		return "redirect:/goals";
	}

	@GetMapping("/device")
	public String device(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			PageDTO pageDTO, @ModelAttribute("cri") Criteria cri, Model model) {

		if (check == null) {
			return "redirect:/login";
		}
		pageDTO.setTotalCount(deviceDao.getCount());

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		model.addAttribute("device", deviceDao.deviceListDao(cri, pageDTO, start, perPageNum));

		Paging pageMaker = new Paging();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(deviceDao.getCount());

		model.addAttribute("pageMaker", pageMaker);

		if (deviceDao.getCount() == 0) {
			model.addAttribute("all", 0);
		} else {
			model.addAttribute("all", 1);
		}

		return "admin07";

	}

	@PostMapping("/device-register")
	public String deviceRegister(@RequestBody String result) {

		LocalDateTime now = LocalDateTime.now();

		String number = "";
		String type = "";
		String memo = "";
		String condition = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		number = element.getAsJsonObject().get("number").getAsString();
		type = element.getAsJsonObject().get("type").getAsString();
		condition = element.getAsJsonObject().get("condition").getAsString();
		memo = element.getAsJsonObject().get("memo").getAsString();

		deviceDao.registerDao(number, type, condition, memo);
		return "redirect:/device";

	}

	@GetMapping("/device-update-modal")
	public String deviceupdatemodal(int number, Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("view", deviceDao.deviceDao(number));
		masterArDeviceId = number;

		return "update_device";
	}

	@PostMapping("/device-update")
	public String deviceupdate(@RequestBody String result) {
		LocalDateTime now = LocalDateTime.now();

		String a = "";
		String b = "";
		String c = "";
		String d = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		a = element.getAsJsonObject().get("number").getAsString();
		b = element.getAsJsonObject().get("type").getAsString();
		c = element.getAsJsonObject().get("condition").getAsString();
		d = element.getAsJsonObject().get("memo").getAsString();

		deviceDao.updateDao(a, b, c, d, masterArDeviceId);

		return "redirect:/device";
	}

	@PostMapping("/device-delete")
	public String deviceDelete(@RequestBody int number) {
		LocalDateTime now = LocalDateTime.now();

		deviceDao.deleteDao(number);

		return "redirect:/device";

	}

	@GetMapping("/device-search")
	public String deviceSearch(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			PageDTO pageDTO, String keyword, Model model, @ModelAttribute("cri") Criteria cri) {

		if (check == null) {
			return "redirect:/login";
		}

		pageDTO.setTotalCount(deviceDao.getSearchCount(keyword));

		int start = cri.getPageStart();
		int perPagenum = cri.getPerPageNum();
		model.addAttribute("list", deviceDao.searchDao(keyword, pageDTO, start, perPagenum, cri));

		Paging pageMaker = new Paging();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(deviceDao.getSearchCount(keyword));

		model.addAttribute("pageMaker", pageMaker);
		model.addAttribute("keyword", keyword);

		if (deviceDao.getSearchCount(keyword) == 0) {
			model.addAttribute("all", 0);
		} else {
			model.addAttribute("all", 1);
		}

		return "admin07_search";
	}

	@GetMapping("/fish")
	public String popup_fish(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", fishDao.fishSearchListDao(year, start, perPageNum));

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(fishDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", fishDao.avgSearchDao(year));

		return "fish";

	}

	@RequestMapping("/yg")
	public String popup_yg(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", ygDao.ygSearchListDao(year, start, perPageNum));

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(ygDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", ygDao.avgSearchDao(year));

		return "yg";

	}

	@RequestMapping("/arfish")
	public String popup_arfish(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", arfishDao.arfishSearchListDao(year, start, perPageNum));

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(arfishDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", arfishDao.avgSearchDao(year));

		return "arfish";

	}

	@RequestMapping("/aryg")
	public String popup_aryg(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", arygDao.arygSearchListDao(year, start, perPageNum));

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(arygDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", arygDao.avgSearchDao(year));

		return "aryg";

	}

	@RequestMapping("/avall")
	public String popup_avall(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", avallDao.avallSearchListDao(year, start, perPageNum, "TBL_LESSONS.BGNG_DT"));

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(avallDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", avallDao.avgSearchDao(year));
		model.addAttribute("avg2", avallDao.avgSearchDao2(year));

		return "avall";

	}

	@RequestMapping("/avall_search")
	public String popup_avall_search(
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String year, String order) {
		if (check == null) {
			return "redirect:/login";
		}
		masterYear = year;
		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		model.addAttribute("list", avallDao.avallSearchListDao(year, start, perPageNum, order));

		model.addAttribute("year", year);
		model.addAttribute("order", order);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(avallDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", avallDao.avgSearchDao(year));
		model.addAttribute("avg2", avallDao.avgSearchDao2(year));
		return "avall_search";

	}

	@GetMapping("/fish_search")
	public String popup_fish_search(
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String year, String mon, String year1) {
		if (check == null) {
			return "redirect:/login";
		}
		masterYear = year;
		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		model.addAttribute("list", fishDao.fishSearchListDao(year, start, perPageNum));

		model.addAttribute("year", year);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(fishDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", fishDao.avgSearchDao(year));

		return "fish_search";

	}

	@RequestMapping("/yg_search")
	public String popup_yg_search(
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String year, String mon, String year1) {
		if (check == null) {
			return "redirect:/login";
		}
		masterYear = year;

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		model.addAttribute("list", ygDao.ygSearchListDao(year, start, perPageNum));

		model.addAttribute("year", year);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(ygDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", ygDao.avgSearchDao(year));

		return "yg_search";

	}

	@RequestMapping("/yg_search2")
	public String yg_search2(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String age, String gender, String home, String year,
			String order) {
		if (check == null) {
			return "redirect:/login";
		}

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", ygDao.ygSearchListDao2(year, start, perPageNum, age, gender, home, order));
		model.addAttribute("year", year);
		model.addAttribute("age", age);
		model.addAttribute("gender", gender);
		model.addAttribute("home", home);
		model.addAttribute("order", order);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(ygDao.getSearchCount2(year, age, gender, home));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", ygDao.avgSearchDao2(year, age, gender, home));

		return "yg_search2";

	}

	@RequestMapping("/arfish_search")
	public String popup_arfish_search(
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String year, String mon, String year1) {
		if (check == null) {
			return "redirect:/login";
		}
		masterYear = year;

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", arfishDao.arfishSearchListDao(year, start, perPageNum));

		model.addAttribute("year", year);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(arfishDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", arfishDao.avgSearchDao(year));

		return "arfish_search";

	}

	@RequestMapping("/arfish_search2")
	public String arfish_search2(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String age, String gender, String home, String year,
			String order) {
		if (check == null) {
			return "redirect:/login";
		}

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();
		if (year == null) {
			LocalDate now = LocalDate.now();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
			year = now.format(formatter);

			age = "0";
			gender = "0";
			home = "0";

		}
		model.addAttribute("list", arfishDao.arfishSearchListDao2(year, start, perPageNum, age, gender, home, order));
		model.addAttribute("year", year);
		model.addAttribute("age", age);
		model.addAttribute("gender", gender);
		model.addAttribute("home", home);
		model.addAttribute("order", order);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(arfishDao.getSearchCount2(year, age, gender, home));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", arfishDao.avgSearchDao2(year, age, gender, home));

		return "arfish_search2";

	}

	@RequestMapping("/aryg_search")
	public String popup_aryg_search(
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String year, String mon, String year1) {
		if (check == null) {
			return "redirect:/login";
		}
		masterYear = year;

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", arygDao.arygSearchListDao(year, start, perPageNum));

		model.addAttribute("year", year);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(arygDao.getSearchCount(year));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", arygDao.avgSearchDao(year));

		return "aryg_search";

	}

	@RequestMapping("/aryg_search2")
	public String aryg_search2(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String age, String gender, String home, String year,
			String order) {
		if (check == null) {
			return "redirect:/login";
		}

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", arygDao.arygSearchListDao2(year, start, perPageNum, age, gender, home, order));
		model.addAttribute("year", year);
		model.addAttribute("age", age);
		model.addAttribute("gender", gender);
		model.addAttribute("home", home);
		model.addAttribute("order", order);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(arygDao.getSearchCount2(year, age, gender, home));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", arygDao.avgSearchDao2(year, age, gender, home));

		return "aryg_search2";

	}

	@RequestMapping("/fish_search2")
	public String popup_fish_search2(
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria2 cri, Model model, String age, String gender, String home, String order,
			String year) {
		if (check == null) {
			return "redirect:/login";
		}

		int start = cri.getPageStart();
		int perPageNum = cri.getPerPageNum();

		model.addAttribute("list", fishDao.fishSearchListDao2(year, start, perPageNum, age, gender, home, order));
		model.addAttribute("year", year);
		model.addAttribute("age", age);
		model.addAttribute("gender", gender);
		model.addAttribute("home", home);
		model.addAttribute("order", order);

		Paging2 pageMaker = new Paging2();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(fishDao.getSearchCount2(year, age, gender, home));

		model.addAttribute("pageMaker", pageMaker);

		model.addAttribute("avg", fishDao.avgSearchDao2(year, age, gender, home));

		return "fish_search2";

	}

	@GetMapping("/fishdownload")
	public void excelDownload(HttpServletResponse response, FishDto fishDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<FishDto> excel = fishDao.fishList(year);

		// Body
		for (int i = 0; i < fishDao.getSearchCount(year); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/fishsearchdownload")
	public void excelfishSearchDownload(HttpServletResponse response, FishDto fishDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<FishDto> excel = fishDao.fishList(masterYear);

		// Body
		for (int i = 0; i < fishDao.getSearchCount(masterYear); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/ygdownload")
	public void excelygDownload(HttpServletResponse response, YgDto ygDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<YgDto> excel = ygDao.ygList(year);

		// Body
		for (int i = 0; i < ygDao.getSearchCount(year); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/ygsearchdownload")
	public void excelygsearchDownload(HttpServletResponse response, YgDto ygDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<YgDto> excel = ygDao.ygList(masterYear);

		// Body
		for (int i = 0; i < ygDao.getSearchCount(masterYear); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/arfishdownload")
	public void arfishexcelDownload(HttpServletResponse response, ArfishDto arfishDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<ArfishDto> excel = arfishDao.arfishList(year);

		// Body
		for (int i = 0; i < arfishDao.getSearchCount(year); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/arfishsearchdownload")
	public void arfishSearchexcelfishSearchDownload(HttpServletResponse response, ArfishDto arfishDto)
			throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<ArfishDto> excel = arfishDao.arfishList(masterYear);

		// Body
		for (int i = 0; i < arfishDao.getSearchCount(masterYear); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/arygdownload")
	public void arygexcelygDownload(HttpServletResponse response, ArygDto arygDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<ArygDto> excel = arygDao.arygList(year);

		// Body
		for (int i = 0; i < arygDao.getSearchCount(year); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/arygsearchdownload")
	public void arygexcelygsearchDownload(HttpServletResponse response, ArygDto yargDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("이용날짜");
		cell = row.createCell(1);
		cell.setCellValue("연령대");
		cell = row.createCell(2);
		cell.setCellValue("성별");
		cell = row.createCell(3);
		cell.setCellValue("거주지");
		cell = row.createCell(4);
		cell.setCellValue("만족도");

		// System.out.println(fishDao.fishList());
		List<ArygDto> excel = arygDao.arygList(masterYear);

		// Body
		for (int i = 0; i < arygDao.getSearchCount(masterYear); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getUSE_DT());
			cell = row.createCell(1);
			if (excel.get(i).getAR_USER_CAT() == 1) {
				cell.setCellValue("미취학");
			} else if (excel.get(i).getAR_USER_CAT() == 2) {
				cell.setCellValue("초등");
			} else if (excel.get(i).getAR_USER_CAT() == 3) {
				cell.setCellValue("중등");
			} else if (excel.get(i).getAR_USER_CAT() == 4) {
				cell.setCellValue("고등");
			} else if (excel.get(i).getAR_USER_CAT() == 5) {
				cell.setCellValue("일반");
			}
			cell = row.createCell(2);
			if (excel.get(i).getGender() == 1) {
				cell.setCellValue("남자");
			} else if (excel.get(i).getGender() == 2) {
				cell.setCellValue("여자");
			}
			cell = row.createCell(3);
			if (excel.get(i).getAddress() == 1) {
				cell.setCellValue("중원구");
			} else if (excel.get(i).getAddress() == 2) {
				cell.setCellValue("수정구");
			} else if (excel.get(i).getAddress() == 3) {
				cell.setCellValue("분당구");
			} else if (excel.get(i).getAddress() == 4) {
				cell.setCellValue("기타");
			}
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
		}

		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/avalldownload")
	public void avallexcelygsearchDownload(HttpServletResponse response, AvallDto avallDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String year = now.format(formatter);
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("수업일시");
		cell = row.createCell(1);
		cell.setCellValue("수업명");
		cell = row.createCell(2);
		cell.setCellValue("강의자");
		cell = row.createCell(3);
		cell.setCellValue("돌봄센터");
		cell = row.createCell(4);
		cell.setCellValue("만족도");
		cell = row.createCell(5);
		cell.setCellValue("참여율");

		// System.out.println(fishDao.fishList());
		List<AvallDto> excel = avallDao.avallList(year);

		// Body
		for (int i = 0; i < avallDao.getSearchCount(year); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getDate());
			cell = row.createCell(1);
			cell.setCellValue(excel.get(i).getLESSON_TTL());
			cell = row.createCell(2);
			cell.setCellValue(excel.get(i).getINSTR_NM());
			cell = row.createCell(3);
			cell.setCellValue(excel.get(i).getCENTER_TTL());
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
			cell = row.createCell(5);
			cell.setCellValue(excel.get(i).getNope());

		}
		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@GetMapping("/avallsearchdownload")
	public void avallSearchexcelygsearchDownload(HttpServletResponse response, AvallDto avallDto) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("첫번째 시트");
		Row row = null;
		Cell cell = null;
		int rowNum = 0;

		// List<java.lang.String> list=fishDao.fishList(fishDto);

		// Header
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("수업일시");
		cell = row.createCell(1);
		cell.setCellValue("수업명");
		cell = row.createCell(2);
		cell.setCellValue("강의자");
		cell = row.createCell(3);
		cell.setCellValue("돌봄센터");
		cell = row.createCell(4);
		cell.setCellValue("만족도");
		cell = row.createCell(5);
		cell.setCellValue("참여율");

		// System.out.println(fishDao.fishList());
		List<AvallDto> excel = avallDao.avallList(masterYear);

		// Body
		for (int i = 0; i < avallDao.getSearchCount(masterYear); i++) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(excel.get(i).getDate());
			cell = row.createCell(1);
			cell.setCellValue(excel.get(i).getLESSON_TTL());
			cell = row.createCell(2);
			cell.setCellValue(excel.get(i).getINSTR_NM());
			cell = row.createCell(3);
			cell.setCellValue(excel.get(i).getCENTER_TTL());
			cell = row.createCell(4);
			cell.setCellValue(excel.get(i).getDGSTFN_SCORE());
			cell = row.createCell(5);
			cell.setCellValue(excel.get(i).getNope());

		}
		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
		response.setHeader("Content-Disposition", "attachment;filename=down.xlsx");

		// Excel File Output
		wb.write(response.getOutputStream());
		wb.close();

	}

	@RequestMapping("/fishupload")
	public @ResponseBody int upload(@RequestParam("file") MultipartFile file) throws IOException {
		int a = 1;
		Date m;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ExcelData> dataList = new ArrayList<>();
		LocalDate now = LocalDate.now();
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
		if (!extension.equals("xlsx") && !extension.equals("xls")) {
			a = 2;
			return a;
		}
		Workbook workbook = null;

		if (extension.equals("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		}

		Sheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4

			Row row = worksheet.getRow(i);

			ExcelData data = new ExcelData();
			String an = "";
			String an1 = "";
			String bn = "";
			String bn1 = "";
			String cn = "";
			String cn1 = "";
			m = row.getCell(0).getDateCellValue();

			an = row.getCell(1).getStringCellValue();

			bn = row.getCell(2).getStringCellValue();
			cn = row.getCell(3).getStringCellValue();
			if (an.equals("미취학")) {
				an1 = "01";
			} else if (an.equals("초등")) {
				an1 = "02";
			} else if (an.equals("중등")) {
				an1 = "03";
			}

			else if (an.equals("고등")) {
				an1 = "04";
			} else if (an.equals("일반")) {
				an1 = "05";
			}
			if (bn.equals("남자")) {
				bn1 = "01";
			} else if (bn.equals("여자")) {
				bn1 = "02";
			}
			if (cn.equals("중원구")) {
				cn1 = "01";
			} else if (cn.equals("수정구")) {
				cn1 = "02";
			} else if (cn.equals("분당구")) {
				cn1 = "03";
			} else if (cn.equals("기타")) {
				cn1 = "04";
			}

			arDao.arRegisterDao(format.format(m), "01", an1, row.getCell(4).getNumericCellValue(), now, bn1, cn1);

		}

		return a;
	}

	@RequestMapping("/ygupload")
	public @ResponseBody int ygupload(@RequestParam("file") MultipartFile file) throws IOException {
		int a = 1;
		Date m;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ExcelData> dataList = new ArrayList<>();
		LocalDate now = LocalDate.now();
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
		if (!extension.equals("xlsx") && !extension.equals("xls")) {
			a = 2;
			return a;
		}
		Workbook workbook = null;

		if (extension.equals("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		}

		Sheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4

			Row row = worksheet.getRow(i);

			ExcelData data = new ExcelData();
			String an = "";
			String an1 = "";
			String bn = "";
			String bn1 = "";
			String cn = "";
			String cn1 = "";
			m = row.getCell(0).getDateCellValue();

			an = row.getCell(1).getStringCellValue();

			bn = row.getCell(2).getStringCellValue();
			cn = row.getCell(3).getStringCellValue();
			if (an.equals("미취학")) {
				an1 = "01";
			} else if (an.equals("초등")) {
				an1 = "02";
			} else if (an.equals("중등")) {
				an1 = "03";
			}

			else if (an.equals("고등")) {
				an1 = "04";
			} else if (an.equals("일반")) {
				an1 = "05";
			}
			if (bn.equals("남자")) {
				bn1 = "01";
			} else if (bn.equals("여자")) {
				bn1 = "02";
			}
			if (cn.equals("중원구")) {
				cn1 = "01";
			} else if (cn.equals("수정구")) {
				cn1 = "02";
			} else if (cn.equals("분당구")) {
				cn1 = "03";
			} else if (cn.equals("기타")) {
				cn1 = "04";
			}

			arDao.arRegisterDao(format.format(m), "02", an1, row.getCell(4).getNumericCellValue(), now, bn1, cn1);

		}

		return a;
	}

	@RequestMapping("/arfishupload")
	public @ResponseBody int arfishupload(@RequestParam("file") MultipartFile file) throws IOException {
		int a = 1;
		Date m;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ExcelData> dataList = new ArrayList<>();
		LocalDate now = LocalDate.now();
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
		if (!extension.equals("xlsx") && !extension.equals("xls")) {
			a = 2;
			return a;
		}
		Workbook workbook = null;

		if (extension.equals("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		}

		Sheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4

			Row row = worksheet.getRow(i);

			ExcelData data = new ExcelData();
			String an = "";
			String an1 = "";
			String bn = "";
			String bn1 = "";
			String cn = "";
			String cn1 = "";
			m = row.getCell(0).getDateCellValue();

			an = row.getCell(1).getStringCellValue();

			bn = row.getCell(2).getStringCellValue();
			cn = row.getCell(3).getStringCellValue();
			if (an.equals("미취학")) {
				an1 = "01";
			} else if (an.equals("초등")) {
				an1 = "02";
			} else if (an.equals("중등")) {
				an1 = "03";
			}

			else if (an.equals("고등")) {
				an1 = "04";
			} else if (an.equals("일반")) {
				an1 = "05";
			}
			if (bn.equals("남자")) {
				bn1 = "01";
			} else if (bn.equals("여자")) {
				bn1 = "02";
			}
			if (cn.equals("중원구")) {
				cn1 = "01";
			} else if (cn.equals("수정구")) {
				cn1 = "02";
			} else if (cn.equals("분당구")) {
				cn1 = "03";
			} else if (cn.equals("기타")) {
				cn1 = "04";
			}

			arDao.arRegisterDao(format.format(m), "11", an1, row.getCell(4).getNumericCellValue(), now, bn1, cn1);

		}

		return a;
	}

	@RequestMapping("/arygupload")
	public @ResponseBody int arygupload(@RequestParam("file") MultipartFile file) throws IOException {
		int a = 1;
		Date m;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ExcelData> dataList = new ArrayList<>();
		LocalDate now = LocalDate.now();
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
		if (!extension.equals("xlsx") && !extension.equals("xls")) {
			a = 2;
			return a;
		}
		Workbook workbook = null;

		if (extension.equals("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (extension.equals("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		}

		Sheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4

			Row row = worksheet.getRow(i);

			ExcelData data = new ExcelData();
			String an = "";
			String an1 = "";
			String bn = "";
			String bn1 = "";
			String cn = "";
			String cn1 = "";
			m = row.getCell(0).getDateCellValue();

			an = row.getCell(1).getStringCellValue();

			bn = row.getCell(2).getStringCellValue();
			cn = row.getCell(3).getStringCellValue();
			if (an.equals("미취학")) {
				an1 = "01";
			} else if (an.equals("초등")) {
				an1 = "02";
			} else if (an.equals("중등")) {
				an1 = "03";
			}

			else if (an.equals("고등")) {
				an1 = "04";
			} else if (an.equals("일반")) {
				an1 = "05";
			}
			if (bn.equals("남자")) {
				bn1 = "01";
			} else if (bn.equals("여자")) {
				bn1 = "02";
			}
			if (cn.equals("중원구")) {
				cn1 = "01";
			} else if (cn.equals("수정구")) {
				cn1 = "02";
			} else if (cn.equals("분당구")) {
				cn1 = "03";
			} else if (cn.equals("기타")) {
				cn1 = "04";
			}

			arDao.arRegisterDao(format.format(m), "12", an1, row.getCell(4).getNumericCellValue(), now, bn1, cn1);

		}

		return a;
	}

	@RequestMapping("/avallupload")
	public @ResponseBody int avallupload(@RequestParam("file") MultipartFile file) throws IOException  {
		int a=1;
		
		
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
		 if (!extension.equals("xlsx") && !extension.equals("xls")) {
		      a=2;
		      return a;
		    }
		 Workbook workbook = null;
		 
		 if (extension.equals("xlsx")) {
		      workbook = new XSSFWorkbook(file.getInputStream());
		    } else if (extension.equals("xls")) {
		      workbook = new HSSFWorkbook(file.getInputStream());
		    }

		    Sheet worksheet = workbook.getSheetAt(0);
		    
		    for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4

		        Row row = worksheet.getRow(i);

		   
		        double an1,an2,an3,an4;
		        Integer a1,a2,a3=null,a4=null;
		       String dd=";";
				an1=row.getCell(0).getNumericCellValue();
				a1=(int)Math.round(an1);
				
		        an2=row.getCell(1).getNumericCellValue();
		        a2=(int)Math.round(an2);
		        
		        
		        
		        if(row.getCell(2)!=null) {
		        	an3=row.getCell(2).getNumericCellValue();
			        a3=(int)Math.round(an3);
		        }
		        
		        
		        if(row.getCell(3)!=null) {
		        	an4=row.getCell(3).getNumericCellValue();
			        a4=(int)Math.round(an4);
		        }

		        if(centerDao.checkDao3(a1)==null){
		        	a=4;
		        }
		        else {
		        	if(centerDao.checkDao2(a1, "050400"+a2)==null) {
		        		centerDao.innsertDao(a1, "050400"+a2, a3, a4);
		        	}
		        	else {
		        		if(a3==null&&a4!=null) {
		        			centerDao.updateDao4(a4
					        		,  a1 ,"050400"+a2);
		        		}
		        		else if(a4==null&&a3!=null) {
		        			centerDao.updateDao5(a3
					        		,  a1 ,"050400"+a2);
		        		}
		        		else if(a3==null&&a4==null) {
		        			
		        		}
		        		else {
		        		 centerDao.updateDao3(a3,a4
					        		,  a1 ,"050400"+a2);
		        		 }
		        	}
		        	
		        	
		        }
		        
		      }
		    
		    
		    return a;
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8"); // 파일 인코딩 설정
		multipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024); // 파일당 업로드 크기 제한 (5MB)
		return multipartResolver;
	}

}