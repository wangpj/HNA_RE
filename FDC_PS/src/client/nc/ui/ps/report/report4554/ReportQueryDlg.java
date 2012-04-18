/**
 * @作者 zhfa
 * @创建时间：2007-5-15 上午11:08:14
 * @修改者：ssd
 * @修改时间：2007-5-15 上午11:08:14
 * @说明：
 */
package nc.ui.ps.report.report4554;

import javax.swing.table.TableCellEditor;

import nc.itf.ps.pub.IDownList;
import nc.ui.bd.manage.UIRefCellEditor;
import nc.ui.ps.pub.query.PSReportQueryDLG;
import nc.ui.ps.pub.ref.BdBuildingRefModel;
import nc.ui.ps.pub.ref.BdHouseRefModel;
import nc.ui.ps.pub.ref.BdSituationRefModel;
import nc.ui.ps.pub.ref.Ps_cmg_customerRefModel;
import nc.ui.ps.report.pub.PSReportPubOpreate;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.fdc.pub.BillDateGetter;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ps.ps2020.PrmDiscountVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.query.QueryConditionVO;

/**
 * @作者 zhfa
 * @创建时间：2007-5-15 上午11:08:14
 * @修改者：ssd
 * @修改时间：2007-5-15 上午11:08:14
 * @说明：
 */
public class ReportQueryDlg extends PSReportQueryDLG {
	private String pk_project = null;
	private String pk_building = null;
	private String m_addWherePart = null;
	public ReportQueryDlg() {
		super();
		 initComboxData();
	}

	/**
	 * QueryDLG 构造子注解。
	 * 
	 * @param parent
	 *            java.awt.Container
	 */
	public ReportQueryDlg(java.awt.Container parent) {
		super(parent);		
		this.hideNormal();
		this.hideUnitButton();
		 initComboxData();
	}

	/**
	 * QueryDLG 构造子注解。
	 * 
	 * @param parent
	 *            java.awt.Container
	 * @param title
	 *            java.lang.String
	 */
	public ReportQueryDlg(java.awt.Container parent, String title) {
		super(parent, title);
		 initComboxData();
	}

	/**
	 * QueryDLG 构造子注解。
	 * 
	 * @param parent
	 *            java.awt.Frame
	 */
	public ReportQueryDlg(java.awt.Frame parent) {
		super(parent);		
		 initComboxData();
	}

	/**
	 * QueryDLG 构造子注解。
	 * 
	 * @param parent
	 *            java.awt.Frame
	 * @param title
	 *            java.lang.String
	 */
	public ReportQueryDlg(java.awt.Frame parent, String title) {
		super(parent, title);	
		 initComboxData();
	}

	
	public String checkCondition() {
		String strRet = super.checkCondition();
		return strRet;
	} 
 
	public void initComboxData(){
		UIComboBox sellPoint = new UIComboBox();
		sellPoint.addItems(IDownList.ISELLPOINT);
		setValueRef("isellpoint", sellPoint);
	}
	
	/**
	 * 
	 * @创建者：zhfa
	 * @方法说明：
	 * @创建时间：2007-5-16 下午05:29:59
	 * @修改者：zhfa
	 * @修改时间：2007-5-16 下午05:29:59
	 * @param cusVO
	 * @return
	 *
	 */
	private String getDiscountPKStr(SuperVO[] discountVos){
		String[] discounttypePks=new String[discountVos.length];
		for(int i=0;i<discounttypePks.length;i++){
			discounttypePks[i]=((PrmDiscountVO)discountVos[i]).getPk_discounttype().trim();
		}
		StringBuffer strBf = new StringBuffer();
		for (int i = 0; i <discounttypePks.length; i++) {
			strBf.append("'");
			strBf.append(discounttypePks[i]);
			strBf.append("',");
		}
		String sqlpk = strBf.toString();
		sqlpk = sqlpk.substring(0, sqlpk.length() - 1);
		return sqlpk;
	}

	
	@Override
	public void initData() {
		super.initData();
		
		initDefaultData();
	}
	
	@Override
	protected void afterEdit(TableCellEditor editor, int row, int col) {


		super.afterEdit(editor, row, col);
		try{
			if (col == COLVALUE && editor instanceof UIRefCellEditor) {
				Object tmp = ((UIRefCellEditor) editor).getComponent();
				if (tmp != null && tmp instanceof UIRefPane) {
					UIRefPane pane = (UIRefPane) tmp;
					if(pane.getRefNodeName().equalsIgnoreCase(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001824")//@res "日历"
))
						return;
					if ( !pane.getRefNodeName().equalsIgnoreCase(NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0004165")//@res "项目"
						 ) && pane.getRefModel().getPkFieldCode().equalsIgnoreCase("pk_project")) {
						if(pane.getRefPK()!=null && pane.getRefPK().trim().length()>0){
							pk_project = pane.getRefPK();
							
							//房产
							UIRefPane refbuild = new UIRefPane();
							BdHouseRefModel houseModel = new BdHouseRefModel(); 
							houseModel.setPk_project(pk_project);
							refbuild.setRefModel(houseModel);
							changeValueRef("ps_cb_gathering.pk_house", refbuild); 
							//楼栋
							UIRefPane ref1 = new UIRefPane();
							BdBuildingRefModel buildModel = new BdBuildingRefModel(); 
							buildModel.setPk_project(new String[] {pk_project});
							buildModel.setDocJoinField("crm_bd_building.pk_project_object");
							ref1.setRefModel(houseModel);
							changeValueRef("crm_bd_house.pk_building", ref1); 
						}else{
							pk_project = null;
						}
					}
					m_addWherePart = null;
				}
			}
			m_addWherePart = null;
			pk_project=null;
		}catch(Exception e){
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
	}

	
	/**
	 * @功能：设置默认查询条件
	 */
	protected void initDefaultData(){
		QueryConditionVO[] convo = getConditionDatas();
		if(convo==null || convo.length<=0){
			return;
		}
		
		for(int i=0;i<convo.length ;i++){
			if("ps_cb_gathering.dchargedate2".equalsIgnoreCase(convo[i].getFieldCode())){
				getUITabInput().setValueAt(BillDateGetter.getBillDate(), i, 4);
			}
		}
	}
}
