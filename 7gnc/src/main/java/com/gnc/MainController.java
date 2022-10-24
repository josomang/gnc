package com.gnc;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gnc.dao.LessonsDao;
import com.gnc.dao.UserDao;
import com.gnc.dto.Criteria;
import com.gnc.dto.LessonsDto;
import com.gnc.dto.Paging;
import com.gnc.dto.lect_listDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class MainController  {

	String key;
	int masterId;
	@Autowired
	UserDao userDao;

	@Autowired
	LessonsDao lessonsDao;

	@Autowired
	CenterDao centerDao;

	@Autowired
	ArDao arDao;

	@GetMapping("/")
	public String root(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		return "redirect:/admin";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "login";
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

	@PostMapping("/logout")
	public String logout(HttpServletRequest request,
			@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check) {

		LocalDateTime now = LocalDateTime.now();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		userDao.use_dtDao(check, now);
		return "redirect:/login";

	}

	@GetMapping("/admin")
	public String home(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model, LessonsDto lessonsDto) {
		if (check == null) {
			return "redirect:/login";
		}

		model.addAttribute("list", lessonsDao.lessonsListDao(cri));

		Paging pageMaker = new Paging();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(lessonsDao.getCount());

		model.addAttribute("pageMaker", pageMaker);

		return "admin01_list";
	}

	@PostMapping("/register")
	public String register(@RequestBody String result) {

		LocalDateTime now = LocalDateTime.now();

		String LESSON_TTL = "";
		String BGNG_DT = "";
		String END_DT = "";
		
		String LESSON_TM = "";
		String ROOM_ID="";
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

		lessonsDao.registerDao(LESSON_TTL, BGNG_DT, END_DT, LESSON_TM,ROOM_ID, INSTR_NM, LESSON_TYPE, LESSON_DESC);

		return "redirect:/admin";
	}

	@PostMapping("/update")
	public @ResponseBody String update(@RequestBody String result) {
		LocalDateTime now = LocalDateTime.now();

		String LESSON_TTL = "";
		String BGNG_DT = "";
		String END_DT = "";
		
		String LESSON_TM = "";
		String ROOM_ID="";
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

		lessonsDao.updateDao(masterId, LESSON_TTL, BGNG_DT, END_DT, LESSON_TM,ROOM_ID ,INSTR_NM, LESSON_TYPE, LESSON_DESC, now);

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
		return "admin01_view";
	}

	@PostMapping("/lect_result")
	public String admin01_view (Model model,@RequestBody String result) {
		
		// List<LessonsDto> a = lessonsDao.lessonsDao(id);
		//model.addAttribute("view", lessonsDao.lessonsDao(id));

		LocalDate now = LocalDate.now();
		int LESSON_ID;
		String CENTER_ID = "";
		int total_count;
		//int NOPE;
		JsonArray center_count;
	

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		
		LESSON_ID = element.getAsJsonObject().get("lecture_id").getAsInt();
		total_count=element.getAsJsonObject().get("total_count").getAsInt();
		//center_count = element.getAsJsonObject().get("center_count").getAsJsonArray();
		
		JsonObject jsonObj = (JsonObject) parser.parse(result);
		
		JsonArray memberArray = (JsonArray) jsonObj.get("center_count");
		for (int i = 0; i < memberArray.size(); i++) {          
			JsonObject object = (JsonObject) memberArray.get(i);
		   String a = object.get("center_id").getAsString();
			int b = object.get("count").getAsInt();
			System.out.println("센터아이디 : " + a);    
			centerDao.centerLessonsInsert(LESSON_ID,a,b);
		}   
		


		return "redirect:/";
	}
	
	@GetMapping("/lect_list")
	public @ResponseBody List<lect_listDto> lect_list() {
		lessonsDao.lect_listDao();
		
		
		return lessonsDao.lect_listDao();
	}

	@RequestMapping("/search")
	public String search(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model, String keyword) {

		if (check == null) {
			return "redirect:/login";
		}
		model.addAttribute("list", lessonsDao.searchDao(keyword, cri));

		Paging pageMaker = new Paging();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(lessonsDao.getSearchCount(keyword));

		model.addAttribute("pageMaker", pageMaker);

		return "admin01_list";
	}

	@GetMapping("/ar")
	public String test(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) 
	String check,Model model) {
		if (check == null) {
			return "redirect:/login";
		}
		model.addAttribute("co",monthSum());
		return "admin02";
	}
	@GetMapping("/ardate")
	public String ardate(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) 
	String check,Model model) {
		if (check == null) {
			return "redirect:/login";
		}

		return "date";
	}
	
	

	@RequestMapping("/monthAll")
	public @ResponseBody ArrayList<Integer> test12() {
		ArrayList<Integer> n = new ArrayList<>();

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
		String day = now.format(formatter);

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
		int n29 = arDao.getCountday29(day + "-29");
		int n30 = arDao.getCountday30(day + "-30");
		int n31 = arDao.getCountday31(day + "-31");

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
		n.add(n29);
		n.add(n30);
		n.add(n31);

		return n;
	}

	@GetMapping("/month-sum")
	public @ResponseBody int monthSum() {
		int sum = 0;
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
		String day = now.format(formatter);
		sum = arDao.getCountday01(day + "-01") + arDao.getCountday02(day + "-02") + arDao.getCountday03(day + "-03")
				+ arDao.getCountday04(day + "-04") + arDao.getCountday05(day + "-05") + arDao.getCountday06(day + "-06")
				+ arDao.getCountday07(day + "-07") + arDao.getCountday08(day + "-08") + arDao.getCountday09(day + "-09")
				+ arDao.getCountday10(day + "-10") + arDao.getCountday11(day + "-11") + arDao.getCountday12(day + "-12")
				+ arDao.getCountday13(day + "-13") + arDao.getCountday14(day + "-14") + arDao.getCountday15(day + "-15")
				+ arDao.getCountday16(day + "-16") + arDao.getCountday17(day + "-17") + arDao.getCountday18(day + "-18")
				+ arDao.getCountday19(day + "-19") + arDao.getCountday20(day + "-20") + arDao.getCountday21(day + "-21")
				+ arDao.getCountday22(day + "-22") + arDao.getCountday23(day + "-23") + arDao.getCountday24(day + "-24")
				+ arDao.getCountday25(day + "-25") + arDao.getCountday26(day + "-26") + arDao.getCountday27(day + "-27")
				+ arDao.getCountday28(day + "-28") + arDao.getCountday29(day + "-29") + arDao.getCountday30(day + "-30")
				+ arDao.getCountday31(day + "-31");

		return sum;
	}

	@PostMapping("/ar_user_log")
	public @ResponseBody void mistake(@RequestBody String result) {
		LocalDate now = LocalDate.now();
		String USE_DT = "";
		String AR_CONTENT_TYPE = "";
		String AR_USER_CATEGORY = "";
		int DGSTFN_SCORE;

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		USE_DT = element.getAsJsonObject().get("datetime").getAsString();
		AR_CONTENT_TYPE = element.getAsJsonObject().get("content_type").getAsString();
		AR_USER_CATEGORY = element.getAsJsonObject().get("user_category").getAsString();
		DGSTFN_SCORE = element.getAsJsonObject().get("dgstfn_score").getAsInt();

		arDao.arRegisterDao(USE_DT, AR_CONTENT_TYPE, AR_USER_CATEGORY, DGSTFN_SCORE, now);

	}

	@GetMapping("/center")
	public String center(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			@ModelAttribute("cri") Criteria cri, Model model) {
		if (check == null) {
			return "redirect:/login";
		}
		model.addAttribute("center", centerDao.centerListDao(cri));

		Paging pageMaker = new Paging();

		pageMaker.setCri(cri);

		pageMaker.setTotalCount(centerDao.getCount());

		model.addAttribute("pageMaker", pageMaker);

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
	public String centerupdatemodal(String CENTER_ID, Model model) {

		model.addAttribute("view", centerDao.centerDao(CENTER_ID));

		return "update";
	}

	@GetMapping("/center-search")
	public String centersearch(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) String check,
			String CENTER_ID, Model model, @ModelAttribute("cri") Criteria cri, String keyword) {

		if (check == null) {
			return "redirect:/login";
		}
		model.addAttribute("center", centerDao.searchDao(keyword, cri));

		Paging pageMaker = new Paging();
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(centerDao.getSearchCount(keyword));

		model.addAttribute("pageMaker", pageMaker);

		return "admin03_list";
	}

	@PostMapping("/center-delete")
	public String centerDelete(@RequestBody String CENTER_ID) {
		centerDao.deleteDao(CENTER_ID);

		return "redirect:/center";

	}

	@GetMapping("/satisfaction")
	public String satisfaction() {

		return "admin04";

	}

	@PostMapping("/delete")
	public String delete() {
		lessonsDao.deleteDao(masterId);

		return "redirect:/admin";

	}

}