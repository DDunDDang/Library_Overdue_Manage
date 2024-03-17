package output;

public class ManagementSum {
    private int manageNum = 0;
    private int[] numberOfMonthlyMessageManagement = new int[13];
    private int massageNum = 0;
    private int[] numberOfMonthlyCallManagement = new int[13];
    private int callNum = 0;
    private int[] numberOfMonthlyETCManagement = new int[13];
    private int etcNum = 0;

    public void increaseManageNum() {
        this.manageNum += 1;
    }

    public void increaseMassageNum(int baseMonth) {
        this.massageNum += 1;
        this.numberOfMonthlyMessageManagement[baseMonth] += 1;
    }

    public void increaseCallNum(int baseMonth) {
        this.callNum += 1;
        this.numberOfMonthlyCallManagement[baseMonth] += 1;
    }

    public void increaseEtcNum(int baseMonth) {
        this.etcNum += 1;
        this.numberOfMonthlyETCManagement[baseMonth] += 1;
    }

    public int getManageNum() {
        return manageNum;
    }

    public void setManageNum(int manageNum) {
        this.manageNum = manageNum;
    }

    public int[] getNumberOfMonthlyMessageManagement() {
        return numberOfMonthlyMessageManagement;
    }

    public void setNumberOfMonthlyMessageManagement(int[] numberOfMonthlyMessageManagement) {
        this.numberOfMonthlyMessageManagement = numberOfMonthlyMessageManagement;
    }

    public int getMassageNum() {
        return massageNum;
    }

    public void setMassageNum(int massageNum) {
        this.massageNum = massageNum;
    }

    public int[] getNumberOfMonthlyCallManagement() {
        return numberOfMonthlyCallManagement;
    }

    public void setNumberOfMonthlyCallManagement(int[] numberOfMonthlyCallManagement) {
        this.numberOfMonthlyCallManagement = numberOfMonthlyCallManagement;
    }

    public int getCallNum() {
        return callNum;
    }

    public void setCallNum(int callNum) {
        this.callNum = callNum;
    }

    public int[] getNumberOfMonthlyETCManagement() {
        return numberOfMonthlyETCManagement;
    }

    public void setNumberOfMonthlyETCManagement(int[] numberOfMonthlyETCManagement) {
        this.numberOfMonthlyETCManagement = numberOfMonthlyETCManagement;
    }

    public int getEtcNum() {
        return etcNum;
    }

    public void setEtcNum(int etcNum) {
        this.etcNum = etcNum;
    }
}
