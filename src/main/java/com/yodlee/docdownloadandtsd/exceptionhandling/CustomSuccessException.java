package com.yodlee.docdownloadandtsd.exceptionhandling;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.OK)

public class CustomSuccessException extends RuntimeException {

	 private String message;

	 private String pathString;

	 private static final long serialVersionUID = 1L;

	

	 public CustomSuccessException(String message) {

		 

	       this.message=message;

	    }

	 public String getMessage() {

	        return message;

	    }



	    public void setMessage(String message) {

	        this.message = message;

	    }

	    

	    public String getPathString() {

			return pathString;

		}

		public void setPathString(String pathString) {

			this.pathString = pathString;

		}

		public CustomSuccessException(String message, String pathString) {

			 

		       this.message=message;

		       this.pathString=pathString;

		    }

}

