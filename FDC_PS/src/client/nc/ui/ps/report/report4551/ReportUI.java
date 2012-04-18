/**
 * @作者 zhfa
 * @创建时间：2007-5-14 上午10:13:37
 * @修改者：ssd
 * @修改时间：2007-5-14 上午10:13:37
 * @说明：
 */
package nc.ui.ps.report.report4551;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nc.itf.jzfdc.pub.report.IUFTypes;
import nc.itf.ps.pub.IPSModuleCode;
import nc.ui.bill.depend.PageInfo;
import nc.ui.fdc.pub.PmUIProxy;
import nc.ui.jzfdc.report.PMLinkQueryData;
import nc.ui.jzfdc.report.buttonactoin.IReportButton;
import nc.ui.ps.pub.PSUIProxy;
import nc.ui.ps.report.pub.PSReportBaseUI;
import nc.ui.pub.FuncNodeStarter;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.ui.sm.power.FuncRegister;
import nc.ui.trade.query.HYQueryConditionDLG;
import nc.ui.trade.report.query.QueryDLG;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.crmbd.psgathering.CbGatheringCVO;
import nc.vo.jzfdc.pub.report.ReportBaseVO;
import nc.vo.jzfdc.pub.report.SubtotalVO;
import nc.vo.ps.pub.util.ReportBaseUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.funcreg.FuncRegisterVO;

/**
 * @作者 zhfa
 * @创建时间：2007-5-14 上午10:13:37
 * @修改者：ssd
 * @修改时间：2007-5-14 上午10:13:37
 * @说明：H3014551	回款明细表	nc.ui.ps.report.report4551.ReportUI
 */
@SuppressWarnings({ "unchecked", "serial","deprecation" })
public class ReportUI extends PSReportBaseUI {

	// 客户与置业顾问的Haspmap,因为一个客户可能对应多个置业顾问，所以value是个Arraylist
	Map m_customNewSellor = new HashMap<String, ArrayList<String>>();
	Map m_customOldSellor = new HashMap<String, ArrayList<String>>();
	Map m_houseSellor = new HashMap<String, String[]>();
	/**
	 * @创建者:zhfa
	 * @创建时间：2007-5-14 上午10:13:38
	 * @修改者：ssd
	 * @修改时间：2007-5-14 上午10:13:38 方法说明：
	 * 
	 */
	private ReportQueryDlg m_qryDlg = null;

	public ReportUI() {
		//排序
		setUnlockSortListener(true);
		reSetModel();
	}

	@Override
	public String _getModelCode() {
		// ZYT 自动生成方法存根
		return IPSModuleCode.DETAIL_CUST_BACKMNY;
	}

	/**
	 * @创建者：zhfa
	 * @方法说明：
	 * @创建时间：2007-5-14 上午10:13:38
	 * @修改者：ssd
	 * @修改时间：2007-5-14 上午10:13:38
	 * @override
	 * 
	 */
	@Override
	public void onQuery() {
		// ZYT 自动生成方法存根
		
		try {
			getQueryDlg2().showModal();
			if (getQueryDlg2().getResult() == UIDialog.ID_OK) {
				execQuery();
				getExecFormulas();
				try {
					total();
				} catch (Exception e) {
					showWarningMessage(e.getMessage());
				}
			}
		} catch (Exception e) {
			showWarningMessage(e.getMessage());
		}
	}
	
	@Override
	protected ReportBaseVO[] queryData() {

		showHeaderVO();

		ReportBaseVO[] reportBodyVOs = null;

		QueryTree currTree = getCurrTree(getQueryDlg2());
		QueryTree powerTree = getPowerTree(getQueryDlg2());
		// **** comment by yf 2009-08-22
		// powerTree.getQueryString();

		try {
			// 需要分页设置
			if (isPage()) {
				// 查询分页的总数和页数
				PageInfo p = getReportAdapter().queryPageInfo(
						getBusiImplClazzName(), currTree, powerTree,
						getUserObject(), getPageInfo());
				if (p != null) {
					// 设置当前的查询页为第一页
					p.setM_iCurrentPage(1);
					getPageNavigationBar().setPageInfo(p);
				}

				reportBodyVOs = getReportAdapter().queryReportVOByPageInfo(
						getBusiImplClazzName(), getCurrTree(getQueryDlg2()),
						getPowerTree(getQueryDlg2()), getUserObject(), p);
			}
			// 不需要分页设置
			else {
				reportBodyVOs = getReportAdapter().queryDataByCond(
						getBusiImplClazzName(), getCurrTree(getQueryDlg2()),
						getPowerTree(getQueryDlg2()), getUserObject());
			}
		} catch (BusinessException e) {

			nc.bs.logging.Logger.error(e.getMessage(), e);
			showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("H0", "UPPH0-002119")/* @res "后台数据查询出现错误！" */);
			return null;
		}

		if (reportBodyVOs != null && reportBodyVOs.length > 0) {
			return reportBodyVOs;
			
		} else {
			return null;
		}

	}
	
	@Override
	public String getBusiImplClazzName() {
		
		return "nc.bs.ps.report.report4551.ReportQueryImpl";
	}
	
	private QueryDlg m_qryDlg2 = null;
	
	@Override
	public HYQueryConditionDLG getQueryDlg2() {
		if (m_qryDlg2 == null) {
			m_qryDlg2 = new QueryDlg(this,null,getTemplateInfo());
			m_qryDlg2.setProjectField("crm_bd_house.pk_project");
			m_qryDlg2.setHouseField("crm_bd_house.pk_house");
			m_qryDlg2.setSituationField("crm_bd_house.pk_situation");
			m_qryDlg2.setPaymodeField("pk_paymode");
		}
		return m_qryDlg2;
	}


	public void onColumnFilter(String title, String[] fieldNames, String[] showNames, boolean isAdjustOrder) throws Exception {
		super.onColumnFilter(title, fieldNames, showNames, isAdjustOrder);
		getReportBase().getBodyPanel().getTable().removeSortListener();
	}

	@Override
//	public int[] getReportButtonAry() {
//		return new int[] { IReportButton.QueryBtn, IReportButton.PrintBtn ,IReportButton.ColumnFilterBtn, IReportButton.SubTotalBtn};
//	}
	public int[] getReportButtonAry() {
		m_buttonArray = new int[] { IReportButton.QueryBtn,
				IReportButton.ColumnFilterBtn, IReportButton.CrossBtn,
				IReportButton.FilterBtn, IReportButton.SortBtn,
				IReportButton.SubTotalBtn, IReportButton.PrintBtn,
				IReportButton.RefreshBtn, };
		return m_buttonArray;
	}
	
	@Override
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		// ZYT 自动生成方法存根
		super.onButtonClicked(bo);
		getExecFormulas();
	}

	private void getExecFormulas() {
//		getReportBase().getBillModel().execFormulas(
//				new String[] { "project->iif(isempty(pk_project),getColValue(fdc_bd_project,vname,pk_project,pk_project),pk_project);",
//						"pk_putmode->getColValue(ps_bd_paymode,vpmname,pk_paymode,signpay);" });

	}

	private void total() throws Exception {

		SubtotalVO svo = new SubtotalVO();
		// 只对实收金额做合计，因为应收款和欠收款合计有问题:
		svo.setGroupFldCanNUll(true);// 分组列的数据是否可以为空。
		svo.setAsLeafRs(new boolean[] { false });// 分组列合并后是否作为末级节点记录。
		svo.setValueFlds(new String[] { "mny" });// 求值列:
		svo.setValueFldTypes(new int[] { IUFTypes.UFD });// 求值列的类型:
		svo.setTotalDescOnFld("project");// 合计所在列
		setSubtotalVO(svo);
		doSubTotal();
	}

	/**
	 * 根据房产和取原置业顾问还是新置业顾问来取客户子表
	 * 
	 * @param pk_house
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unused")
	private String[] getSellor(String pk_house) throws Exception {
		if (m_houseSellor.get(pk_house) != null)
			return (String[]) m_houseSellor.get(pk_house);
		Map newSellorMap = new HashMap<String, String>();
		Map oldSellorMap = new HashMap<String, String>();
		String strWhere = " isnull(dr,0)=0 and pk_gathering in (select pk_gathering from ps_cb_gathering where isnull(ps_cb_gathering.dr,0)=0 and pk_house='"
				+ pk_house + "')  ";
		CbGatheringCVO[] gatheringCVOs = (CbGatheringCVO[]) PSUIProxy.getIUifService().queryByCondition(CbGatheringCVO.class, strWhere);
		if (gatheringCVOs == null || gatheringCVOs.length == 0)
			return null;
		String newSellor = "";
		String oldSellor = "";
		for (int i = 0; i < gatheringCVOs.length; i++) {
			ArrayList<ArrayList> sellorlist = getSell(gatheringCVOs[i].getPk_customer());
			ArrayList<String> newsellorlist = sellorlist.get(0);
			for (String string : newsellorlist) {
				if (newSellorMap.get(string) == null) {
					newSellorMap.put(string, string);
				}
			}
			ArrayList<String> oldsellorlist = sellorlist.get(1);
			for (String string2 : oldsellorlist) {
				if (oldSellorMap.get(string2) == null) {
					oldSellorMap.put(string2, string2);
				}
			}
		}
		Iterator it1 = newSellorMap.keySet().iterator();
		while (it1.hasNext()) {
			if (newSellor == null || newSellor.trim().length() == 0) {
				newSellor += newSellorMap.get(it1.next());
			} else {
				newSellor += "," + newSellorMap.get(it1.next());
			}
		}
		Iterator it2 = oldSellorMap.keySet().iterator();
		while (it2.hasNext()) {
			if (oldSellor == null || oldSellor.trim().length() == 0) {
				oldSellor += oldSellorMap.get(it2.next());
			} else {
				oldSellor += "," + oldSellorMap.get(it2.next());
			}
		}
		m_customOldSellor.put(pk_house, new String[] { newSellor, oldSellor });
		return new String[] { newSellor, oldSellor };
	}

	/**
	 * 根据客户得到置业顾问，存入类变量customerMap中
	 * 
	 * @param pkcustomer
	 * @throws Exception
	 */
	private ArrayList<ArrayList> getSell(String pkcustomer) throws Exception {
		// 如果映射关系中已经有此客户，那么返回
		if (m_customNewSellor.get(pkcustomer) != null || m_customOldSellor.get(pkcustomer) != null) {
			ArrayList<ArrayList> alal = new ArrayList<ArrayList>();
			ArrayList<String> newdal = new ArrayList<String>();
			ArrayList<String> oldal = new ArrayList<String>();
			if (m_customNewSellor.get(pkcustomer) != null) {
				newdal = (ArrayList<String>) m_customNewSellor.get(pkcustomer);
			}
			if (m_customOldSellor.get(pkcustomer) != null) {
				oldal = (ArrayList<String>) m_customOldSellor.get(pkcustomer);
			}
			alal.add(newdal);
			alal.add(oldal);
			return alal;
		}
		String whereString = " select bd_psndoc.psnname,ps_cmg_customer_sellor.bcsellornow from bd_psndoc  inner join ps_cmg_customer_sellor  on ps_cmg_customer_sellor.pk_psndoc=bd_psndoc.pk_psndoc "
				+ " and ps_cmg_customer_sellor.pk_customer='"
				+ pkcustomer
				+ "' and isnull(ps_cmg_customer_sellor.dr,0)=0 and isnull(bd_psndoc.dr,0)=0 ";
		ReportBaseVO[] psndocVOs = PmUIProxy.getIReportService().queryVOBySql(whereString);
		if (psndocVOs == null || psndocVOs.length == 0)
			return null;
		ArrayList<ArrayList> alal = new ArrayList<ArrayList>();
		ArrayList<String> newdal = new ArrayList<String>();
		ArrayList<String> oldal = new ArrayList<String>();
		for (int j = 0; j < psndocVOs.length; j++) {
			if ("Y".equalsIgnoreCase(psndocVOs[j].getAttributeValue("bcsellornow").toString())) {
				if (!newdal.contains(psndocVOs[j].getAttributeValue("psnname").toString())) {
					newdal.add(psndocVOs[j].getAttributeValue("psnname").toString());
				}
			} else {
				if (!oldal.contains(psndocVOs[j].getAttributeValue("psnname").toString())) {
					oldal.add(psndocVOs[j].getAttributeValue("psnname").toString());
				}
			}
		}
		m_customNewSellor.put(pkcustomer, newdal);
		m_customOldSellor.put(pkcustomer, oldal);
		alal.add(newdal);
		alal.add(oldal);
		return alal;
	}

	@Override
	public QueryDLG getQueryDlg() {
		// ZYT 自动生成方法存根
		if (m_qryDlg == null) {
			m_qryDlg = new ReportQueryDlg(this);
			m_qryDlg.setTempletID(_getCorpID(), _getModelCode(), _getUserID(), null);

		}
		return m_qryDlg;
	}

	/**
	 * @创建者：zhfa
	 * @方法说明：
	 * @创建时间：2007-5-14 上午10:13:38
	 * @修改者：ssd
	 * @修改时间：2007-5-14 上午10:13:38
	 * @override
	 * 
	 */
	@Override
	public void setUIAfterLoadTemplate() {

	}

	@Override
	public boolean isPage() {
		return false;
	}
	
	@Override
	public String[] getSpecialPenerateCode() {
		return new String[]{"vbillno"};
	}
	
	@Override
	public void onPenerate() {
		ReportBaseVO rowVo = getSelectedVO();
		String[] penecolumn={"vbillno"};
		String[] destFunCodearr={IPSModuleCode.CB_GATHERING};
		String destFunCode="";
		String itemkey = getItemKey();
		String pk_gathering = rowVo.getAttributeValue("pk_gathering")==null?" ":rowVo.getAttributeValue("pk_gathering").toString();
		PMLinkQueryData linkdata = new PMLinkQueryData();
		StringBuffer sb = new StringBuffer();
		if(itemkey.equals(penecolumn[0])){
			sb.append(" isnull(ps_cb_gathering.dr,0)=0 ");
			sb.append(" and ps_cb_gathering.pk_gathering='" + pk_gathering + "'");
			destFunCode = destFunCodearr[0];
		}
		linkdata.setUserObject(sb.toString());
		FuncRegisterVO frVO = SFClientUtil.findFRVOFromMenuTree(destFunCode);
		String pkCorp = _getCorpID();
		if (frVO == null && pkCorp != null && (!destFunCode.equals(""))&& ReportBaseUtil.hasPower(pkCorp,destFunCode)){			
			frVO = FuncRegister.getFuncRegisterVOByCode(destFunCode);
		}
		if (frVO != null){		
			FuncNodeStarter.openDialog(frVO, ILinkType.LINK_TYPE_QUERY,linkdata, this, false, true);
			showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH0-002127")/*@res "穿透成功！"*/);
		}else{
			showHintMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("H0","UPPH10301-000629")/*@res "无对应的穿透信息"*/);
		}
	}

}