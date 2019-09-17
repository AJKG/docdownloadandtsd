package com.yodlee.docdownloadandtsd.DAO;

import com.mongodb.DBCollection;
import com.yodlee.docdownloadandtsd.VO.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;


@Repository
public class DBAccessRepositoryImpl implements MongoAccess {

    private final MongoTemplate mongoTemplate;

    public DBAccessRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public DocResponseVO AddDocResponseToDB(DocResponseVO docResponseVO){

        Query query = new Query();
        query.addCriteria(Criteria.where("sum_info_id").is(docResponseVO.getSumInfoId()));

        mongoTemplate.findAllAndRemove(query, "DocDownload");
        mongoTemplate.save(docResponseVO);


   return docResponseVO;
}

    @Override
    public TSDResponseVO AddTSDResponseToDB(TSDResponseVO tsdResponseVO){

        Query query = new Query();
        query.addCriteria(Criteria.where("sum_info_id").is(tsdResponseVO.getSumInfoId()));

        mongoTemplate.findAllAndRemove(query, "TSD");
        mongoTemplate.save(tsdResponseVO);



        return tsdResponseVO;
    }

    @Override
    public SumInfoVO AddCacheResponseToDB(SumInfoVO sumInfoVO){

        Query query = new Query();
        query.addCriteria(Criteria.where("sum_info_id").is(sumInfoVO.getSum_info_id()));

        mongoTemplate.findAllAndRemove(query, "CacheResponse");
        mongoTemplate.save(sumInfoVO);


        return sumInfoVO;
    }

    @Override
    public FirememExtractedResponseForDocumentDownload AddUserResponse(FirememExtractedResponseForDocumentDownload firememExtractedResponseForDocumentDownload){

        mongoTemplate.save(firememExtractedResponseForDocumentDownload);


        return firememExtractedResponseForDocumentDownload;
    }

    @Override
    public FirememExtractedResponseForTSD AddUserResponse(FirememExtractedResponseForTSD firememExtractedResponseForTSD){

        mongoTemplate.save(firememExtractedResponseForTSD);


        return firememExtractedResponseForTSD;
    }

    @Override
    public List<DocResponseVO> getDocResponseFromDB(String sumInfo, boolean reCert) throws Exception{

        List<DocResponseVO> docMap = null;

            Query query = new Query();
            if(reCert){
                query.addCriteria(Criteria.where("reCert").exists(true));
            }else{
                query.addCriteria(Criteria.where("reCert").exists(false));
            }


            if (sumInfo == null || sumInfo.toLowerCase().contains("all")) {

                docMap = mongoTemplate.find(query, DocResponseVO.class, "DocDownload");

            } else if (sumInfo.matches("\\d+")) {
                query.addCriteria(Criteria.where("_id").is(sumInfo));

                docMap = mongoTemplate.find(query, DocResponseVO.class, "DocDownload");

            } else {
                throw new Exception("Input provided is not in proper format");
            }

        return docMap;
    }

    @Override
    public List<TSDResponseVO> getTSDResponseFromDB(String sumInfo) throws Exception{

        List<TSDResponseVO> TSDMap = null;

        if(sumInfo == null || sumInfo.toLowerCase().contains("all")){
              TSDMap = mongoTemplate.findAll(TSDResponseVO.class,"TSD");

        }else if(sumInfo.matches("\\d+")) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(sumInfo));

           TSDMap = mongoTemplate.find(query, TSDResponseVO.class, "TSD");

        }else{
            throw new Exception("Input provided is not in proper format");
        }
        return TSDMap;
    }

    @Override
    public List<SumInfoVO> getCacheResponseFromDB(String sumInfo) throws Exception{

        List<SumInfoVO> CacheMap = null;

        if(sumInfo == null || sumInfo.toLowerCase().contains("all")){
            CacheMap = mongoTemplate.findAll(SumInfoVO.class,"CacheResponse");

        }else if(sumInfo.matches("\\d+")) {
            Query query = new Query();
            query.addCriteria(Criteria.where("sum_info_id").is(sumInfo));

            CacheMap = mongoTemplate.find(query, SumInfoVO.class, "CacheResponse");

        }else{
            throw new Exception("Input provided is not in proper format");
        }
        return CacheMap;
    }


    @Override
    public List<ABSListVO> getABSListFromDB(){

        List<ABSListVO> ABSList = mongoTemplate.findAll(ABSListVO.class,"ABSList");

        return ABSList;
    }

    @Override
    public List<TTRBandingVO> getTTRBandFromDB(){

        List<TTRBandingVO> TTRBand = mongoTemplate.findAll(TTRBandingVO.class,"TTRBanding");

        return TTRBand;
    }

    @Override
    public SplunkItemDetailsVO AddUserResponse(SplunkItemDetailsVO splunkItemDetailsVO){

        mongoTemplate.save(splunkItemDetailsVO);


        return splunkItemDetailsVO;
    }

}
