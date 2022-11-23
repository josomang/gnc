package com.gnc;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.gnc.dao.ArDao;
import com.gnc.dao.CenterDao;
import com.gnc.dao.DeviceDao;
import com.gnc.dao.LessonsDao;
import com.gnc.dao.LibraryDao;
import com.gnc.dao.UserDao;

import com.gnc.service.kpiService;

import com.gnc.dto.Criteria;

import com.gnc.dto.PageDTO;
import com.gnc.dto.Paging;
import com.gnc.dto.goalsDto;
import com.gnc.dto.lect_listDto;
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
	kpiService kpiService;
	

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
	public String login(@RequestBody String result, BindingResult bindingResult, HttpServletRequest request) {
		LocalDateTime now = LocalDateTime.now();
		String UID = "";
		String PSWD = "";

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		UID = element.getAsJsonObject().get("UID").getAsString();
		PSWD = element.getAsJsonObject().get("PSWD").getAsString();

		String check = userDao.getUserAccount(UID, PSWD);

		if (check == null) {
			bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
			return "login";
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(SessionConstants.LOGIN_MEMBER, check);
			userDao.last_lgn_dtDao(check, now);

			return "redirect:/admin";
		}

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
	public @ResponseBody String admin01_view(Model model, @RequestBody String result) {
		System.out.println(result);
		LocalDate now = LocalDate.now();
		int LESSON_ID;
		String CENTER_ID = "";
		int total_count;
		// int NOPE;
		JsonArray center_count;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		
		LESSON_ID = element.getAsJsonObject().get("lecture_id").getAsInt();
		total_count = element.getAsJsonObject().get("total_count").getAsInt();
		// center_count =
		// element.getAsJsonObject().get("center_count").getAsJsonArray();

		JsonObject jsonObj = (JsonObject) parser.parse(result);

		JsonArray memberArray = (JsonArray) jsonObj.get("center_count");
		
		System.out.println(result);

		for (int i = 0; i < memberArray.size(); i++) {
			JsonObject object = (JsonObject) memberArray.get(i);
			String a = object.get("center_id").getAsString();
			int b = object.get("count").getAsInt();

			//centerDao.centerLessonsInsert(LESSON_ID, "050400"+a, b);
		}
		if (LESSON_ID != 0) {
			return result;
		} else {
			return result;
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
	public String graph(Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}
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

		if (start == end) {
			list.add(start);
			list2.add(libraryDao.getLibraryPeopleDao(Integer.toString(start)));
			dd.put("month", list);
			dd.put("point", list2);
		}

		// 여기서부터 해야함 년으로 받았을 때 같은 년도는 해결
		else {
			for (int i = start; i < end + 1; i++) {
				list.add(i);
				list2.add(libraryDao.getLibraryPeopleDao(Integer.toString(i)));
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
	public @ResponseBody String mistake(@RequestBody String result) {
		LocalDate now = LocalDate.now();
		String USE_DT = "";
		String AR_CONTENT_TYPE = "";
		String AR_USER_CATEGORY = "";
		Integer DGSTFN_SCORE;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		USE_DT = element.getAsJsonObject().get("datetime").getAsString();
		AR_CONTENT_TYPE = element.getAsJsonObject().get("content_type").getAsString();
		AR_USER_CATEGORY = element.getAsJsonObject().get("user_category").getAsString();

		if (element.getAsJsonObject().get("dgstfn_score").isJsonNull()) {

			DGSTFN_SCORE = null;
		} else {
			DGSTFN_SCORE = element.getAsJsonObject().get("dgstfn_score").getAsInt();

		}

		arDao.arRegisterDao(USE_DT, AR_CONTENT_TYPE, AR_USER_CATEGORY, DGSTFN_SCORE, now);

		return "성공";
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

	@PostMapping("/center-register")
	public String centerRegister(@RequestBody String result) {

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

		centerDao.registerDao(CENTER_TTL, CENTER_ID, CENTER_UID, CENTER_PSWD, CENTER_MAX_NOPE);

		return "redirect:/admin";
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
	public String dgs(String year, Model model,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		if (check == null) {
			return "redirect:/login";
		}

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

		HashMap<String, List<Integer>> dd = new HashMap<>();
		List<Integer> list = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();

		list.add(Integer.parseInt(year));
		list2.add(libraryDao.getLibraryPeopleDao(year));
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

		deviceDao.deleteDao(now, number);

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

	@GetMapping("/ee")
	public @ResponseBody String kpi(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,HttpServletRequest request) {

		kpiService.kpi();
		
		return "redirect:/admin";
	}
	
	

	@GetMapping("/test")
	public @ResponseBody Object testService() {
		Object a = null;
		JsonObject obj = new JsonObject();
		Object dw = null;
		obj.add("class_utztn_rate", (JsonElement) dw);
		return obj.toString();
	}

}