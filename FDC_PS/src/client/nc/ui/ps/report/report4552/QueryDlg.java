package nc.ui.ps.report.report4552;

import java.awt.Container;
import java.awt.Frame;

import javax.swing.table.TableCellEditor;

import nc.ui.bd.manage.UIRefCellEditor;
import nc.ui.ps.pub.ref.BdHouseRefModel;
import nc.ui.ps.report.pub.ReportQueryDlg;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.fdc.pub.BillDateGetter;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.query.QueryConditionVO;

public class QueryDlg extends ReportQueryDlg {
	//add by zhaohf time 2012-03-27 start
	private String pk_project = null;
	private String pk_building = null;
	private String m_addWherePart = null;
	//add by zhaohf time 2012-03-27 end
	public QueryDlg() {
		// TODO 自动生成构造函数存根
	}

	public QueryDlg(Container parent) {
		super(parent);
		// TODO 自动生成构造函数存根
	}

	public QueryDlg(Container parent, String title) {
		super(parent, title);
		// TODO 自动生成构造函数存根
	}

	public QueryDlg(Frame parent) {
		super(parent);
		// TODO 自动生成构造函数存根
	}

	public QueryDlg(Frame parent, String title) {
		super(parent, title);
		// TODO 自动生成构造函数存根
	}
	
	@Override
	public void initData() {
		super.initData();
		
		initDefaultData();
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
			if("edate".equalsIgnoreCase(convo[i].getFieldCode())){
				getUITabInput().setValueAt(BillDateGetter.getBillDate(), i, 4);
			}
		}
	}
	
	/**
	 * @创建者：zhfa
	 * @方法说明：
	 * @创建时间：2007-5-16 下午04:58:10
	 * @修改者：zhfa
	 * @修改时间：2007-5-16 下午04:58:10
	 * @param editor
	 * @param row
	 * @param col
	 * @override
	 *
	 */
	@Override
	protected void afterEdit(TableCellEditor editor, int row, int col) { 
 
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
					 
							UIRefPane refbuild = new UIRefPane();
							BdHouseRefModel houseModel = new BdHouseRefModel();
							 
							houseModel.setPk_project(pk_project);
							refbuild.setRefModel(houseModel);
							changeValueRef("pk_house", refbuild); 
							
						}else{
							pk_project = null;
						}
					}
					m_addWherePart = null;
				}
			}
			super.afterEdit(editor, row, col);
			m_addWherePart = null;
			pk_project=null;
		}catch(Exception e){
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
	}

}
