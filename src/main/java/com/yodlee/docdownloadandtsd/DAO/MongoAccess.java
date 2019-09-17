package com.yodlee.docdownloadandtsd.DAO;

import com.mongodb.DBObject;
import com.yodlee.docdownloadandtsd.VO.*;

import java.util.HashMap;
import java.util.List;


public interface MongoAccess {


     DocResponseVO AddDocResponseToDB(DocResponseVO docResponseVO);
     FirememExtractedResponseForDocumentDownload AddUserResponse(FirememExtractedResponseForDocumentDownload firememExtractedResponseForDocumentDownload);
     TSDResponseVO AddTSDResponseToDB(TSDResponseVO tsdResponseVO);
     FirememExtractedResponseForTSD AddUserResponse(FirememExtractedResponseForTSD firememExtractedResponseForTSD);
     List<DocResponseVO> getDocResponseFromDB(String sumInfo, boolean reCert) throws Exception;
     List<TSDResponseVO> getTSDResponseFromDB(String sumInfo) throws Exception;
     List<ABSListVO> getABSListFromDB();
     List<TTRBandingVO> getTTRBandFromDB();
     SumInfoVO AddCacheResponseToDB(SumInfoVO sumInfoVO);
     SplunkItemDetailsVO AddUserResponse(SplunkItemDetailsVO splunkItemDetailsVO);
     List<SumInfoVO> getCacheResponseFromDB(String sumInfo) throws Exception;

}
