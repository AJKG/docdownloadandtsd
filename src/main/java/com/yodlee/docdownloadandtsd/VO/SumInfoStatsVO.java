package com.yodlee.docdownloadandtsd.VO;

public class SumInfoStatsVO {

    private String sum_info_id;
    private String total_request;
    private String success_percentage;

    public String getSum_info_id() {
        return sum_info_id;
    }
    public void setSum_info_id(String sum_info_id) {
        this.sum_info_id = sum_info_id;
    }

    public String getTotal_request() {
        return total_request;
    }
    public void setTotal_request(String total_request) {
        this.total_request = total_request;
    }

    public String getSuccess_percentage() { return success_percentage; }
    public void setSuccess_percentage(String success_percentage) {
        this.success_percentage = success_percentage;
    }

}
