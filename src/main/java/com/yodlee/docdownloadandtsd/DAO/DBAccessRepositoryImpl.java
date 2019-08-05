package com.yodlee.docdownloadandtsd.DAO;

import com.mongodb.DBCollection;
import com.yodlee.docdownloadandtsd.VO.DocResponseVO;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForDocumentDownload;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForTSD;
import com.yodlee.docdownloadandtsd.VO.TSDResponseVO;
import org.springframework.data.mongodb.core.MongoTemplate;
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

       mongoTemplate.save(docResponseVO);


   return docResponseVO;
}

    @Override
    public TSDResponseVO AddTSDResponseToDB(TSDResponseVO tsdResponseVO){

        mongoTemplate.save(tsdResponseVO);


        return tsdResponseVO;
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
    public List<DocResponseVO> getDocResponseFromDB(){

        List<DocResponseVO> docMap = mongoTemplate.findAll(DocResponseVO.class,"DocDownload");

        return docMap;
    }

    @Override
    public List<TSDResponseVO> getTSDResponseFromDB(){

        List<TSDResponseVO> TSDMap = mongoTemplate.findAll(TSDResponseVO.class,"TSD");

        return TSDMap;
    }


}
