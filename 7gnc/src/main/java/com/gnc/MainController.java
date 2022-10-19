package com.gnc;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


@Controller
public class MainController {
	
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
	public String root(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,required=false)
	String check) {
		
			return "redirect:/admin";
	}

	@GetMapping("/login")
	public String loginForm() {
			return "login";
	}
	
	@PostMapping("/login")
	public String login(@RequestBody String result,  BindingResult bindingResult, 
			HttpServletRequest request) {
		 LocalDateTime now = LocalDateTime.now();
		String UID ="";
		String PSWD ="";
		
		JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);
        UID = element.getAsJsonObject().get("UID").getAsString();
        PSWD = element.getAsJsonObject().get("PSWD").getAsString();
		
		String check = userDao.getUserAccount(UID, PSWD);
		
		
		if(check== null) {
			bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
			return "login";
		}
		else {
			HttpSession session = request.getSession();
			session.setAttribute(SessionConstants.LOGIN_MEMBER, check);
			userDao.last_lgn_dtDao(check,now);
			
			return "redirect:/admin";
		}
		
		
	}
	
	

	@PostMapping("/logout")
	public String logout(HttpServletRequest request,
			@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,required=false)
	String check) {
		
		LocalDateTime now = LocalDateTime.now();
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		userDao.use_dtDao(check,now);
		return "redirect:/login";
		
	}
	

	@GetMapping("/admin")
	public String home(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,required=false)
	String check	,@ModelAttribute("cri")Criteria cri,Model model,LessonsDto lessonsDto) {
		if(check == null) {
			return "redirect:/login";
		}
		
		
		model.addAttribute("list",lessonsDao.lessonsListDao(cri));
		
		
		
		Paging pageMaker = new Paging();
	    
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(lessonsDao.getCount());
		
		
		
	
		
		model.addAttribute("pageMaker", pageMaker);
		
		
		
		return "admin01_list";
	}



	@PostMapping("/register")
	public String register(@RequestBody String result) {
		
		LocalDateTime now = LocalDateTime.now();
		
		String LESSON_TTL ="";
		String BGNG_DT ="";
		String END_DT ="";
		String LESSON_TM ="";
		String INSTR_NM ="";
		String LESSON_TYPE ="";
		String LESSON_DESC ="";
		
		
		
		
		JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);
        LESSON_TTL = element.getAsJsonObject().get("LESSON_TTL").getAsString();
        BGNG_DT = element.getAsJsonObject().get("BGNG_DT").getAsString();
        END_DT = element.getAsJsonObject().get("END_DT").getAsString();
        LESSON_TM = element.getAsJsonObject().get("LESSON_TM").getAsString();
        INSTR_NM = element.getAsJsonObject().get("INSTR_NM").getAsString();
        LESSON_TYPE = element.getAsJsonObject().get("LESSON_TYPE").getAsString();
        LESSON_DESC = element.getAsJsonObject().get("LESSON_DESC").getAsString();
		
		lessonsDao.registerDao(LESSON_TTL, BGNG_DT,
				END_DT, LESSON_TM,
				INSTR_NM, LESSON_TYPE, LESSON_DESC);

		return "redirect:/admin";
	}
	
	
	@PostMapping("/update")
	public @ResponseBody String update(@RequestBody String result) {
LocalDateTime now = LocalDateTime.now();
		
		String LESSON_TTL ="";
		String BGNG_DT ="";
		String END_DT ="";
		String LESSON_TM ="";
		String INSTR_NM ="";
		String LESSON_TYPE ="";
		String LESSON_DESC ="";
		
		
		
		
		JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);
        LESSON_TTL = element.getAsJsonObject().get("LESSON_TTL").getAsString();
        BGNG_DT = element.getAsJsonObject().get("BGNG_DT").getAsString();
        END_DT = element.getAsJsonObject().get("END_DT").getAsString();
        LESSON_TM = element.getAsJsonObject().get("LESSON_TM").getAsString();
        INSTR_NM = element.getAsJsonObject().get("INSTR_NM").getAsString();
        LESSON_TYPE = element.getAsJsonObject().get("LESSON_TYPE").getAsString();
        LESSON_DESC = element.getAsJsonObject().get("LESSON_DESC").getAsString();
		
		lessonsDao.updateDao(masterId,LESSON_TTL, BGNG_DT,
				END_DT, LESSON_TM,
				INSTR_NM, LESSON_TYPE, LESSON_DESC,now);

		return "redirect:/view";
	}
	@RequestMapping("/popup")
	public String popup(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,required=false)
	String check,String date,Model model) {
		if(check == null) {
			return "redirect:/login";
		}
		model.addAttribute("ar01_10",arDao.getCount01_10(date));
		model.addAttribute("ar01_11",arDao.getCount01_11(date));
		model.addAttribute("ar01_12",arDao.getCount01_12(date));
		model.addAttribute("ar01_13",arDao.getCount01_13(date));
		model.addAttribute("ar01_14",arDao.getCount01_14(date));
		model.addAttribute("ar01_15",arDao.getCount01_15(date));
		model.addAttribute("ar01_16",arDao.getCount01_16(date));
		model.addAttribute("ar01_17",arDao.getCount01_17(date));
		
		model.addAttribute("ar02_10",arDao.getCount02_10(date));
		model.addAttribute("ar02_11",arDao.getCount02_11(date));
		model.addAttribute("ar02_12",arDao.getCount02_12(date));
		model.addAttribute("ar02_13",arDao.getCount02_13(date));
		model.addAttribute("ar02_14",arDao.getCount02_14(date));
		model.addAttribute("ar02_15",arDao.getCount02_15(date));
		model.addAttribute("ar02_16",arDao.getCount02_16(date));
		model.addAttribute("ar02_17",arDao.getCount02_17(date));
		
		model.addAttribute("ar11_10",arDao.getCount11_10(date));
		model.addAttribute("ar11_11",arDao.getCount11_11(date));
		model.addAttribute("ar11_12",arDao.getCount11_12(date));
		model.addAttribute("ar11_13",arDao.getCount11_13(date));
		model.addAttribute("ar11_14",arDao.getCount11_14(date));
		model.addAttribute("ar11_15",arDao.getCount11_15(date));
		model.addAttribute("ar11_16",arDao.getCount11_16(date));
		model.addAttribute("ar11_17",arDao.getCount11_17(date));
		
		model.addAttribute("ar12_10",arDao.getCount12_10(date));
		model.addAttribute("ar12_11",arDao.getCount12_11(date));
		model.addAttribute("ar12_12",arDao.getCount12_12(date));
		model.addAttribute("ar12_13",arDao.getCount12_13(date));
		model.addAttribute("ar12_14",arDao.getCount12_14(date));
		model.addAttribute("ar12_15",arDao.getCount12_15(date));
		model.addAttribute("ar12_16",arDao.getCount12_16(date));
		model.addAttribute("ar12_17",arDao.getCount12_17(date));
		
		model.addAttribute("arsum_10",arDao.getCount01_10(date)+arDao.getCount02_10(date)+arDao.getCount11_10(date)+arDao.getCount12_10(date));
		model.addAttribute("arsum_11",arDao.getCount01_11(date)+arDao.getCount02_11(date)+arDao.getCount11_11(date)+arDao.getCount12_11(date));
		model.addAttribute("arsum_12",arDao.getCount01_12(date)+arDao.getCount02_12(date)+arDao.getCount11_12(date)+arDao.getCount12_12(date));
		model.addAttribute("arsum_13",arDao.getCount01_13(date)+arDao.getCount02_13(date)+arDao.getCount11_13(date)+arDao.getCount12_13(date));
		model.addAttribute("arsum_14",arDao.getCount01_14(date)+arDao.getCount02_14(date)+arDao.getCount11_14(date)+arDao.getCount12_14(date));
		model.addAttribute("arsum_15",arDao.getCount01_15(date)+arDao.getCount02_15(date)+arDao.getCount11_15(date)+arDao.getCount12_15(date));
		model.addAttribute("arsum_16",arDao.getCount01_16(date)+arDao.getCount02_16(date)+arDao.getCount11_16(date)+arDao.getCount12_16(date));
		model.addAttribute("arsum_17",arDao.getCount01_17(date)+arDao.getCount02_17(date)+arDao.getCount11_17(date)+arDao.getCount12_17(date));
		
		
		
		
		
		
		
		return "popup";
		
		
	}
	
	@RequestMapping("/view")
	public String view(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,
			required=false)String check, int LESSON_ID,Model model) {
		if(check == null) {
			return "redirect:/login";
		}
		model.addAttribute("view",lessonsDao.lessonsDao(LESSON_ID));
		masterId=LESSON_ID;
		return "admin01_view";
	}
	

	@RequestMapping("/lect_result")
	public String admin01_view(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,
	required=false)
	String check ,int id,Model model,LessonsDto lessonsDto) {
		if(check == null) {
			return "redirect:/login";
		}
		//List<LessonsDto> a =  lessonsDao.lessonsDao(id);
		model.addAttribute("view",lessonsDao.lessonsDao(id));
		
		masterId=id;
		
		
		//System.out.println(id);
		//System.out.println(lessonsDao.lessonsDao(id));
		//System.out.print(lessonsDao.lessonsDao(lessonsDto.getId()));
		
		return "admin01_view";
	}

	
	@RequestMapping("/search")
	public String search(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,required=false)
	String check,@ModelAttribute("cri")Criteria cri,Model model,String keyword) {
	
		if(check == null) {
			return "redirect:/login";
		}
		model.addAttribute("list", lessonsDao.searchDao(keyword,cri));
		
		
		
		
		Paging pageMaker = new Paging();
		pageMaker.setCri(cri);
		
		pageMaker.setTotalCount(lessonsDao.getSearchCount(keyword));
		
		
		
		model.addAttribute("pageMaker", pageMaker);
		
		
		return "admin01_list";
	}
	
	@GetMapping("/ar")
	public String test(@SessionAttribute(name=SessionConstants.LOGIN_MEMBER,required=false)
	String check) {
		if(check == null) {
			return "redirect:/login";
		}
		return "admin02";
	}
	
	
	@PostMapping("/ar_user_log")
	public @ResponseBody void mistake(@RequestBody String result) {
		 LocalDate now = LocalDate.now();
		String USE_DT ="";
		String AR_CONTENT_TYPE ="";
		String AR_USER_CATEGORY ="";
		int DGSTFN_SCORE;
		
		
		JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);
        USE_DT = element.getAsJsonObject().get("datetime").getAsString();
        AR_CONTENT_TYPE = element.getAsJsonObject().get("content_type").getAsString();
        AR_USER_CATEGORY = element.getAsJsonObject().get("user_category").getAsString();
        DGSTFN_SCORE = element.getAsJsonObject().get("dgstfn_score").getAsInt();
       
        
		
		arDao.arRegisterDao(USE_DT, AR_CONTENT_TYPE,
				AR_USER_CATEGORY, DGSTFN_SCORE,
				now);
		
	}
	
	@GetMapping("/center")
	public String center(@SessionAttribute(name=SessionConstants.
	LOGIN_MEMBER,required=false)
	String check,@ModelAttribute("cri")Criteria cri,Model model) {
		if(check == null) {
			return "redirect:/login";
		}
		model.addAttribute("center",centerDao.centerListDao(cri));
		
		
		
		Paging pageMaker = new Paging();
	    
		pageMaker.setCri(cri);

		pageMaker.setTotalCount(centerDao.getCount());
		
		
		model.addAttribute("pageMaker", pageMaker);
		
		
		
		return "admin03_list";
	}
	@PostMapping("/center-register")
	public String centerRegister(@RequestBody String result) {
		
		LocalDateTime now = LocalDateTime.now();
		
		String CENTER_TTL ="";
		String CENTER_ID ="";
		String CENTER_UID ="";
		String CENTER_PSWD ="";
		int CENTER_MAX_NOPE;
		
		
		
		
		JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(result);
        CENTER_TTL = element.getAsJsonObject().get("CENTER_TTL").getAsString();
        CENTER_ID = element.getAsJsonObject().get("CENTER_ID").getAsString();
        CENTER_UID = element.getAsJsonObject().get("CENTER_UID").getAsString();
        CENTER_PSWD = element.getAsJsonObject().get("CENTER_PSWD").getAsString();
        CENTER_MAX_NOPE = element.getAsJsonObject().get("CENTER_MAX_NOPE").getAsInt();
        
		
		centerDao.registerDao(CENTER_TTL, CENTER_ID,
				CENTER_UID, CENTER_PSWD,
				CENTER_MAX_NOPE);

		return "redirect:/admin";
	}
	@GetMapping("/center-update-modal")
	public String centerupdatemodal(String CENTER_ID,Model model) {
		
		model.addAttribute("view",centerDao.centerDao(CENTER_ID));
		
		

		return "update";
	}
	@GetMapping("/center-search")
	public String centersearch(
			@SessionAttribute(name=SessionConstants.
			LOGIN_MEMBER,required=false)
			String check,		
			String CENTER_ID,Model model,
			@ModelAttribute("cri")Criteria cri,String keyword) {
		
		
		if(check == null) {
			return "redirect:/login";
		}
		model.addAttribute("center",centerDao.searchDao(keyword,cri));
		
		
		
		
		Paging pageMaker = new Paging();
		pageMaker.setCri(cri);
		
		pageMaker.setTotalCount(centerDao.getSearchCount(keyword));
		
		
		
		model.addAttribute("pageMaker", pageMaker);

		return "admin03_list";
	}
	
	@PostMapping("/center-delete")
	public String centerDelete(@RequestBody String CENTER_ID ) {
		centerDao.deleteDao(CENTER_ID);
		
		return "redirect:/center";
		
	}
	@GetMapping("/satisfaction")
	public String satisfaction( ) {
		
		
		return "admin04";
		
	}
	
	
	@PostMapping("/delete")
	public String delete() {
		lessonsDao.deleteDao(masterId);
		
		return "redirect:/admin";
		
		
	}
	
}