package nc.ui.ps.report.report4545;
import java.util.ArrayList;
import java.util.HashMap;

import nc.itf.jzfdc.pub.report.IUFTypes;
import nc.itf.ps.pub.IPSModuleCode;
import nc.ui.fdc.pub.PmUIProxy;
import nc.ui.jzfdc.report.buttonactoin.IReportButton;
import nc.ui.ps.report.pub.PSReportBaseUI;
import nc.ui.ps.report.pub.PSReportPubOpreate;
import nc.ui.pub.beans.UIDialog;
import nc.ui.trade.report.query.QueryDLG;
import nc.vo.jzfdc.pub.report.ReportBaseVO;
import nc.vo.jzfdc.pub.report.SubtotalVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.query.ConditionVO;

/**
 * @作者 陶明
 * @创建时间：2007-5-14 上午09:46:36
 * @说明：房产资料统计表
 * H3014545	房产资料统计表	nc.ui.ps.report.report4545.ReportUI
 */
@SuppressWarnings("deprecation")
public class ReportUI extends PSReportBaseUI {

	private static final long serialVersionUID = 1L;

	private PSQueryHouseDetailDlg m_qryDlg = null;

	public ReportUI() {
	}

	@Override
	public String _getModelCode() {
		return IPSModuleCode.CALCULATION_HOUSEINFO;
	}


	public void onColumnFilter(String title, String[] fieldNames,
			String[] showNames, boolean isAdjustOrder) throws Exception {
		super.onColumnFilter(title, fieldNames, showNames, isAdjustOrder);
		getReportBase().getBodyPanel().getTable().removeSortListener();
	}

	@Override
	public void onQuery() {
		try {
			getQueryDlg().showModal();
			if (getQueryDlg().getResult() == UIDialog.ID_OK) {
				execQuery();
			}
		} catch (Exception e) {
			showWarningMessage(e.getMessage());
		}
	}
	

	protected void execQuery(){
		showHeaderVO();

		String querySql = getQueryDlg().getWhereSQL();
		try {
			queryByCustomWhereClause(querySql);
			total();
		} catch (Exception e) {
			showWarningMessage(e.getMessage());
		}
	}

	@Override
	public int[] getReportButtonAry() {
		return new int[] { IReportButton.QueryBtn, IReportButton.PrintBtn,
				IReportButton.ColumnFilterBtn };
	}

	private void total() throws Exception {

		// 合计
		SubtotalVO svo = new SubtotalVO();
		svo.setGroupFldCanNUll(true);// 分组列的数据是否可以为空。
		svo.setAsLeafRs(new boolean[] { false });// 分组列合并后是否作为末级节点记录。

		svo.setValueFlds(new String[] { "nbuildarea", "ninarea", "nrbuildarea",
				"nrinarea", "htotalprice", "hfinaltotalprice" });// 求值列:
		svo.setValueFldTypes(new int[] { IUFTypes.UFD, IUFTypes.UFD,
				IUFTypes.UFD, IUFTypes.UFD, IUFTypes.UFD, IUFTypes.UFD });// 求值列的类型:
		svo.setTotalDescOnFld("project");// 合计所在列
		setSubtotalVO(svo);
		doSubTotal();
	}

	private String getHeadVO(ReportBaseVO retVO) throws Exception {
		ConditionVO[] conditionVO = getQueryDlg().getConditionVO();
		String queryWhere = null;
		for (int i = 0; i < conditionVO.length; i++) {
			if (conditionVO[i].getFieldCode().equals("crm_bd_house.pk_project")) {
				retVO
						.setAttributeValue("pk_project", conditionVO[i]
								.getValue());
				if (queryWhere == null || queryWhere.trim().length() == 0)
					queryWhere = " crm_bd_house.pk_project_build in("
							+ new PSReportPubOpreate()
									.getChildProjPKByWherePart(conditionVO[i]
											.getValue()) + ")";
				else
					queryWhere += " and crm_bd_house.pk_project_build in("
							+ new PSReportPubOpreate()
									.getChildProjPKByWherePart(conditionVO[i]
											.getValue()) + ")";
			} else if (conditionVO[i].getFieldCode().equals(
					"crm_bd_house.pk_situation")) {
				retVO.setAttributeValue("pk_situation", conditionVO[i]
						.getValue());
				if (queryWhere == null || queryWhere.trim().length() == 0)
					queryWhere = " crm_bd_house.pk_situation='"
							+ conditionVO[i].getValue() + "'";
				else
					queryWhere += " and crm_bd_house.pk_situation='"
							+ conditionVO[i].getValue() + "'";
			} else if (conditionVO[i].getFieldCode().equals(
					"crm_bd_house.pk_building")) {
				retVO.setAttributeValue("pk_building", conditionVO[i]
						.getValue());
				if (queryWhere == null || queryWhere.trim().length() == 0)
					queryWhere = " crm_bd_house.pk_building='"
							+ conditionVO[i].getValue() + "'";
				else
					queryWhere += " and crm_bd_house.pk_building='"
							+ conditionVO[i].getValue() + "'";

			} else if (conditionVO[i].getFieldCode().equals(
					"crm_bd_house.pk_sellstate")) {
				retVO.setAttributeValue("pk_sellstate", conditionVO[i]
						.getValue());
				if (queryWhere == null || queryWhere.trim().length() == 0)
					queryWhere = " crm_bd_house.pk_sellstate='"
							+ conditionVO[i].getValue() + "'";
				else
					queryWhere += " and crm_bd_house.pk_sellstate='"
							+ conditionVO[i].getValue() + "'";
			} else if (conditionVO[i].getFieldCode().equals(
					"crm_bd_house.vhcell")) {
				retVO.setAttributeValue("vhcell", conditionVO[i].getValue());
				if (queryWhere == null || queryWhere.trim().length() == 0)
					queryWhere = " crm_bd_house.vhcell='"
							+ conditionVO[i].getValue() + "'";
				else
					queryWhere += " and crm_bd_house.vhcell='"
							+ conditionVO[i].getValue() + "'";
			}
		}

		//add by chixy 2010-05-13 过滤特殊房产
		if(queryWhere !=null && queryWhere.length()>0)
			queryWhere += " and crm_bd_house.pk_sellstate!='0001ZZ1000000001DM9M' ";

		return queryWhere;
	}

	@Override
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		// ZYT 自动生成方法存根
		super.onButtonClicked(bo);
		getReportBase()
				.getBillModel()
				.execFormulas(
						new String[] { "hdis->iif(hfinaltotalprice=htotalprice,0,(hfinaltotalprice/htotalprice)*100)",
								"nbuildmny->htotalprice/nbuildarea;",
								"ninmny->htotalprice/ninarea;",
								"nrbuildmny->htotalprice/nrbuildarea;",
								"nrinmny->htotalprice/nrinarea;",
								"hfinalprice->hfinaltotalprice/nbuildarea"});
	}

	protected void queryByCustomWhereClause(String querySql) throws Exception {
		try {
			ReportBaseVO headVO = new ReportBaseVO();
			String queryWhere = getHeadVO(headVO);
			if (querySql == null || querySql.equals("")) {
				querySql = " 1=1";
			} else {
				querySql = queryWhere;
			}
			ReportBaseVO[] vos = newFuntion(getSQL(querySql));
			vos = this.arrangeQueue(vos);
			getReportBase().setHeadDataVO(headVO);
			getReportBase().execHeadEditFormulas();
			setBodyVO(rebuildVO(vos));
			getReportBase().getBillModel().execLoadFormula();
			getReportBase()
					.getBillModel()
					.execFormulas(
							new String[] { "hdis->iif(hfinaltotalprice=htotalprice,0,(hfinaltotalprice/htotalprice)*100)",
									"nbuildmny->htotalprice/nbuildarea;",
									"ninmny->htotalprice/ninarea;",
									"nrbuildmny->htotalprice/nrbuildarea;",
									"nrinmny->htotalprice/nrinarea;",
									"hfinalprice->hfinaltotalprice/nbuildarea"});
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
			throw new nc.vo.pub.BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001665")//@res "查询时出现错误！"
);
		}
	}

	public ReportBaseVO[] rebuildVO(ReportBaseVO[] baseVO) {
		HashMap<String, ArrayList<ReportBaseVO>> curMap = new HashMap<String, ArrayList<ReportBaseVO>>();
		ArrayList<ReportBaseVO> curList = null;
		for (ReportBaseVO tmpVO : baseVO) {
			if (curMap.containsKey(tmpVO.getAttributeValue("pk_building")
					.toString())) {
				curMap.get(tmpVO.getAttributeValue("pk_building").toString())
						.add(tmpVO);
			} else {
				curList = new ArrayList<ReportBaseVO>();
				curList.add(tmpVO);
				curMap.put(tmpVO.getAttributeValue("pk_building").toString(),
						curList);
			}
		}
		curList = new ArrayList<ReportBaseVO>();
		ReportBaseVO curVO = null;
		for (String strPK : curMap.keySet()) {
			curVO = new ReportBaseVO();
			curVO.setAttributeValue("pk_building", strPK);
			curList.add(curVO);
			for (ReportBaseVO tmpVO : curMap.get(strPK)) {
				tmpVO.setAttributeValue("pk_project_build", "");
				tmpVO.setAttributeValue("pk_building", "");
				curList.add(tmpVO);
			}
		}
		ReportBaseVO[] retVO = new ReportBaseVO[curList.size()];
		for (int i = 0; i < curList.size(); i++) {
			retVO[i] = curList.get(i);

		}
		return retVO;
	}

	// add by zhang feng ，合并相同房产的排号
	@SuppressWarnings("unchecked")
	public ReportBaseVO[] arrangeQueue(ReportBaseVO[] baseVO) {

		HashMap<String, ReportBaseVO> vosmap = new HashMap();

		int size = baseVO.length;
		String pk_house = null;
		String v1 = null;
		String v2 = null;

		for (int i = 0; i <size; i++) {

			pk_house = (String) baseVO[i].getAttributeValue("pk_house");

			//当是同一个房产时
			if (vosmap.get(pk_house) != null && !vosmap.isEmpty()) {

				v1 = (String) vosmap.get(pk_house).getAttributeValue("vfinallybargainnum");
				v2 = (String) baseVO[i].getAttributeValue("vfinallybargainnum");

				if(v2!=null && !"".equals(v2)){

					if(v1!=null && !"".equals(v1)){
						vosmap.get(pk_house).setAttributeValue("vfinallybargainnum", v1+","+v2);
					}else if(v1==null || "".equals(v1)){
						vosmap.get(pk_house).setAttributeValue("vfinallybargainnum", v2);
					}
				}
			}
			//不是同一个房产时
			else{

				vosmap.put(pk_house, baseVO[i]);
			}
		}

		ReportBaseVO[] vosTemp = new ReportBaseVO[vosmap.size()];
		vosmap.values().toArray(vosTemp);

		return vosTemp;
	}

	private String getSQL(String querySql) {
		StringBuffer bf=new StringBuffer();
		bf.append(" select 	crm_bd_house.vhname as hname, crm_bd_house.vhcell as vhcell ,");
		bf.append(" crm_bd_house.vhnum as hnum, crm_bd_house.pk_house as pk_house, ");
		bf.append(" (select fdc_bd_operstate.vname from fdc_bd_operstate where fdc_bd_operstate.pk_operstate = crm_bd_house.pk_situation and isnull(fdc_bd_operstate.dr,0)=0 ) as hsit,");
		bf.append("	case crm_bd_house.fsellmethod  ");
		bf.append(" when 0 then '建筑面积' when 1 then '套内面积' when 2 then '套（个）' else '' ");
//		bf.append("	when 0 then  '");
//		bf.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001814"));
//		bf.append("' when 1 then '");
//		bf.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001815"));
//		bf.append("' when 2 then  '");
//		bf.append(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001816"));
//		bf.append("' else ");
//		bf.append(" '' ");
		bf.append(" end as hselltype,") ;
		bf.append(" (select crm_bd_sellstate.vssname from crm_bd_sellstate where crm_bd_sellstate.pk_sellstate = crm_bd_house.pk_sellstate and isnull(crm_bd_sellstate.dr,0)=0) as hsellstate, ");
		bf.append(" crm_bd_house.nbuildarea,") ;
		bf.append(" crm_bd_house.ninarea, ") ;
		bf.append(" crm_bd_house.nrbuildarea,") ;
		bf.append(" crm_bd_house.nrinarea,") ;
		bf.append(" crm_bd_house.pk_room, ") ;
		bf.append(" crm_bd_house.nsellarea, ") ;
		bf.append(" crm_bd_house.pk_project_build,") ;
		bf.append(" crm_bd_house.pk_building,") ;
		bf.append(" isnull(crm_bd_price.nprice, crm_bd_price.nyprice) as hprice, ") ;
		bf.append(" isnull(crm_bd_price.nmny,crm_bd_price.nymny) as htotalprice,  ") ;
		bf.append(" (crm_bd_house.nbalancemny/crm_bd_house.nsellarea) as hfinalprice, ") ;
		bf.append(" crm_bd_house.nbalancemny as hfinaltotalprice, ") ;
		bf.append(" ps_so_queue.vfinallybargainnum as vfinallybargainnum  ") ;
		bf.append(" from crm_bd_house  ") ;
		bf.append(" left join crm_bd_price  ") ;
		bf.append(" on crm_bd_price.pk_house = crm_bd_house.pk_house  ") ;
		bf.append(" and isnull(crm_bd_price.dr,0)=0   ") ;
		bf.append(" left join ps_so_queue_confine on ps_so_queue_confine.pk_house = crm_bd_house.pk_house and isnull(ps_so_queue_confine.dr,0)=0   ") ;
		bf.append(" left join ps_so_queue on ps_so_queue_confine.pk_queue = ps_so_queue.pk_queue and isnull(ps_so_queue.dr,0)=0 and ps_so_queue.vbillstatus=1 ") ;
		bf.append(" where isnull(crm_bd_house.dr,0)=0   and bissplit = 'N' and bisunite='N' ") ;
		bf.append(" and ") ;
		bf.append(querySql);
		bf.append(" order by crm_bd_house.vhname");

		return bf.toString();
	}

	private ReportBaseVO[] newFuntion(String sql) throws Exception {
		return PmUIProxy.getIReportService().queryVOBySql(sql);
	}

	@Override
	public void setUIAfterLoadTemplate() {

	}

	@Override
	public QueryDLG getQueryDlg() {
		if (m_qryDlg == null) {
			m_qryDlg = new PSQueryHouseDetailDlg(this);
			m_qryDlg.setTempletID(_getCorpID(), _getModelCode(), _getUserID(),
					null);
		}
		return m_qryDlg;
	}

	protected String[][] getOtherItemDigitShowNum() {

		return new String[][] {
				{ "nbuildarea", "ninarea", "nrbuildarea", "nrinarea",
						"nsellarea" }, { "3", "3", "3", "3", "3" } };

	}

	@Override
	public boolean isPage() {
		return false;
	}
}