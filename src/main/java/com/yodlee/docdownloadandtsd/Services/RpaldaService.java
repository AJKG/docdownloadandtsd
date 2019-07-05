package com.yodlee.docdownloadandtsd.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;

import java.util.List;

@Service
@CacheConfig(cacheNames={"differences"})
public class RpaldaService {

    @Autowired
    RpaldaRepository rpaldaRepository;

    @Cacheable
    public List<Object> getDiff() throws Exception {
        return rpaldaRepository.getDiff();
    }

}