package com.yodlee.docdownloadandtsd.DAO;

import com.mongodb.DBObject;
import com.yodlee.docdownloadandtsd.VO.DocResponseVO;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForDocumentDownload;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForTSD;
import com.yodlee.docdownloadandtsd.VO.TSDResponseVO;

import java.util.HashMap;
import java.util.List;


public interface MongoAccess {


     DocResponseVO AddDocResponseToDB(DocResponseVO docResponseVO);
     FirememExtractedResponseForDocumentDownload AddUserResponse(FirememExtractedResponseForDocumentDownload firememExtractedResponseForDocumentDownload);
     TSDResponseVO AddTSDResponseToDB(TSDResponseVO tsdResponseVO);
     FirememExtractedResponseForTSD AddUserResponse(FirememExtractedResponseForTSD firememExtractedResponseForTSD);
     List<DocResponseVO> getDocResponseFromDB();
     List<TSDResponseVO> getTSDResponseFromDB();


}
