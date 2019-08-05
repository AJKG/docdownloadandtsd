package com.yodlee.docdownloadandtsd.Services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yodlee.docdownloadandtsd.VO.DocResponseVO;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class ObjectResponseToJSON {

    public String DocResponseToJson(DocResponseVO docResponseVO) throws Exception{

        String jsonStr = null;
        ObjectMapper Obj = new ObjectMapper();

        try {

            // get Oraganisation object as a json string
            jsonStr = Obj.writeValueAsString(docResponseVO);

            // Displaying JSON String
            System.out.println(jsonStr);
        }catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStr;
    }
}

