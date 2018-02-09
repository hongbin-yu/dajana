package dajana.controler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionTranslator {

	private static final Logger logger = LogManager.getLogger(ControllerExceptionTranslator.class);

	@ExceptionHandler(Exception.class)
	public String exception(Exception e,HttpServletRequest request) {
		//model.put("error", e.getMessage());
		logger.error(e);
		request.setAttribute("error", e.getMessage());
	    return "error";
	}

}