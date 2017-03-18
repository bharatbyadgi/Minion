package networks.focusmind.com.model;


public class AmcDetailDataModel extends BaseDataModel {

    private int id;
    private String startDate;
    private String endDate;
    private String amt;
    private String description;
    private String vendorName;
    private String quotationId;

    public AmcDetailDataModel(int id, String startDate, String endDate, String amt, String description, String vendorName, String quotationId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amt = amt;
        this.description = description;
        this.vendorName = vendorName;
        this.quotationId = quotationId;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

}
