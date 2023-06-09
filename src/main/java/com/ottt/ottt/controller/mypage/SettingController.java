package com.ottt.ottt.controller.mypage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ottt.ottt.dto.UserDTO;
import com.ottt.ottt.service.user.UserService;

@Controller
@RequestMapping("/mypage")
public class SettingController {
		
	@Autowired
	UserService us;
		
	//마이페이지 프로필 셋팅
	@GetMapping(value = "/setting")
	public String mysetting() {
				
	return "/mypage/myprofile/setting";		
	}
	
	//프로필 변경
	@GetMapping(value = "/setting/myprofile")
	public String myprofile(Model m, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String user_id = (String) session.getAttribute("id");
		
		try {
			UserDTO userDTO = us.getUser(user_id);
			m.addAttribute(userDTO);			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		
	return "/mypage/myprofile/myprofile";
	}
	
	//닉네임 변경
	@PostMapping("/setting/myprofile")
	public String myprofile(UserDTO userDTO, String user_nicknm, RedirectAttributes rattr
							, Model m, HttpSession session) {
		
		String user_id = (String) session.getAttribute("id");		
		
		try {
			userDTO = us.getUser(user_id);
			if(us.selectNicknmCnt(user_nicknm) != 0)
				throw new Exception("nicknm duplicate");
			
			userDTO.setUser_nicknm(user_nicknm);
			if(us.mod_nick(userDTO) != 1)
				throw new Exception("mod_nick failed");
				
			rattr.addFlashAttribute("msg","MOD_OK");
			return "redirect:/mypage/setting/myprofile";
		} catch (Exception e) {			
			e.printStackTrace();
			m.addAttribute(userDTO);
			m.addAttribute("msg","MOD_ERR");
			return "/mypage/myprofile/myprofile";
		}		
	
	}	
	
	//내정보 변경
	@GetMapping(value = "/setting/myinfo")
	public String myinfo(Model m, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String user_id = (String) session.getAttribute("id");
		Integer user_no = (Integer) session.getAttribute("user_no");
		
		try {
			UserDTO userDTO = us.getUser(user_id);
			m.addAttribute(userDTO);			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	return "/mypage/myprofile/myinfo";
	}
	
	//비밀번호 변경
	@PostMapping("/setting/myinfo")
	public String pwdchange (UserDTO userDTO, String user_pwd, String re_pwd, 
			RedirectAttributes rattr, Model m, HttpSession session) {
		
		String user_id = (String) session.getAttribute("id");
		try {
			userDTO = us.getUser(user_id);
			
			if(!user_pwd.equals(re_pwd))
				throw new Exception("not same");
			
			userDTO.setUser_pwd(user_pwd);
			if(us.mod_pwd(userDTO) != 1)
				throw new Exception("mod_pwd failed");			

			rattr.addFlashAttribute("msg","CHG_OK");
			return "redirect:/mypage/setting";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute(userDTO);
			m.addAttribute("msg","CHG_ERR");
			return "/mypage/myprofile/myinfo";
		}

	}
	
	//회원탈퇴
	@PostMapping("/setting/goodbye")
	public String goodbye(RedirectAttributes rattr
			, Model m, HttpSession session) {
		
		String user_id = (String) session.getAttribute("id");
		Integer user_no = (Integer) session.getAttribute("no");
		
		try {
			if(us.bye(user_no, user_id) != 1)
				throw new Exception("delete failed");
				
			rattr.addFlashAttribute("msg","byebye");
			session.invalidate();
			return "redirect:/";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("msg","hi");
			return "/mypage/myprofile/setting";
		}

	}

}
