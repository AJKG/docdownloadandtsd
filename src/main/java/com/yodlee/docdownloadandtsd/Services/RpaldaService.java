package com.yodlee.docdownloadandtsd.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;

import java.util.ArrayList;

@Service
public class RpaldaService {


    @Autowired
    RpaldaRepository rpaldaRepository;

    public ArrayList<String> getDiff() throws Exception {
        return rpaldaRepository.getDiff();
    }

}
