/**
 * @���ߣ�����
 * @ʱ�䣺2007-3-5 ����03:29:30
 * @���ܣ�ǩԼ
 */
package nc.ui.ps.ps3530;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.swing.event.ChangeEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.ps.pub.IDownList;
import nc.itf.ps.pub.IPSBusiness;
import nc.itf.ps.pub.IPSModuleCode;
import nc.itf.ps.pub.IPsDataMapping;
import nc.itf.ps.pub.ISellStatus;
import nc.ui.bd.ref.FdcPubRefLinkAddData;
import nc.ui.bd.ref.IBillFillNewData;
import nc.ui.bd.ref.IRefLinkAddData;
import nc.ui.jzfdc.pub.button.FDCBodyImgBtn;
import nc.ui.jzfdc.pub.currency.CurrencyBusinessDelegator;
import nc.ui.ps.pub.HouseStatesMapping;
import nc.ui.ps.pub.PSOpenNodePubData;
import nc.ui.ps.pub.PSUIProxy;
import nc.ui.ps.pub.SellPowerCtrl;
import nc.ui.ps.pub.bsdelegate.PSBusinessDelegator;
import nc.ui.ps.pub.customer.CustomerUtil;
import nc.ui.ps.pub.customer.PenerateToCustomer;
import nc.ui.ps.pub.customer.QuickAddCusImgBtn;
import nc.ui.ps.pub.eventhandler.ManageEventHandler;
import nc.ui.ps.pub.ref.BdFitmentRefModel;
import nc.ui.ps.pub.ref.BdHouseRefModel;
import nc.ui.ps.pub.ref.CustRefUICreator;
import nc.ui.ps.pub.ref.PaymodeRefModel;
import nc.ui.ps.pub.ref.PrmDiscountRefGridTreeModel;
import nc.ui.ps.pub.ref.Ps_cmg_customerRefModel;
import nc.ui.ps.pub.ui.CourseConversionMultiChildBillManagerUI;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.linkoperate.ILinkAddData;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.AbstractManageController;
import nc.ui.trade.bill.BillTemplateWrapper;
import nc.ui.trade.bsdelegate.BusinessDelegator;
import nc.ui.trade.button.IBillButton;
import nc.ui.trade.buttonstate.LineBtnVO;
import nc.vo.crmbd.psgathering.CbGatheringVO;
import nc.vo.crmbd.pub.BdHouseVO;
import nc.vo.crmbd.pub.BdProjectVO;
import nc.vo.fdc.pub.BillDateGetter;
import nc.vo.fdc.pub.SafeObject;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ps.ps0508.BdFitmemtdocBVO;
import nc.vo.ps.ps0540.BdFundsetVO;
import nc.vo.ps.ps2030.SoDmltnprcprgBVO;
import nc.vo.ps.ps2030.SoDmltnprcprgVO;
import nc.vo.ps.ps3510.SoCourseBVO;
import nc.vo.ps.ps3515.SoQueueVO;
import nc.vo.ps.ps3520.SoEngageVO;
import nc.vo.ps.ps3525.SoAppendageVO;
import nc.vo.ps.ps3525.SoSubscDiscountVO;
import nc.vo.ps.ps3525.SoSubscFitmentVO;
import nc.vo.ps.ps3525.SoSubscVO;
import nc.vo.ps.ps3530.HYBillVO;
import nc.vo.ps.ps3530.SoSignCustomerVO;
import nc.vo.ps.ps3530.SoSignDiscountVO;
import nc.vo.ps.ps3530.SoSignMortgageVO;
import nc.vo.ps.ps3530.SoSignVO;
import nc.vo.ps.pub.BdPriceVO;
import nc.vo.ps.pub.CmgCustomerVO;
import nc.vo.ps.pub.CubasdocVO;
import nc.vo.ps.pub.CumandocVO;
import nc.vo.ps.pub.SafeCompute;
import nc.vo.ps.pub.button.AddNewBtn;
import nc.vo.ps.pub.button.AppendageChange;
import nc.vo.ps.pub.button.ChangeContractBtn;
import nc.vo.ps.pub.button.ChangeDiscountBtn;
import nc.vo.ps.pub.button.ContractAuditBtn;
import nc.vo.ps.pub.button.CreateCourseBtn;
import nc.vo.ps.pub.button.DiscountBtn;
import nc.vo.ps.pub.button.HomeMadeBtn;
import nc.vo.ps.pub.button.IPSButton;
import nc.vo.ps.pub.button.QueueButton;
import nc.vo.ps.pub.button.ReceiveBtn;
import nc.vo.ps.pub.button.SoEngageButton;
import nc.vo.ps.pub.button.SoSubscButton;
import nc.vo.ps.pub.paramreader.PSParamReader;
import nc.vo.ps.pub.util.PsCommonUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

/**
 * @���ߣ�����
 * @ʱ�䣺2007-3-5 ����03:29:30
 * @���ܣ�ǩԼ
 */
public class ClientUI extends CourseConversionMultiChildBillManagerUI implements BillCardBeforeEditListener, IBillFillNewData{
	private static final long serialVersionUID = 1L;
	// ���ִ�����
	CurrencyBusinessDelegator cy = null;
	private Integer OPEREATE = IPsDataMapping.OPEREATE_UPDATE;
	private boolean isChange = false;// �Ƿ�������ۿ�����!
	private boolean payModeChange = false;// �Ƿ�����˸��ʽ

	private boolean fitmentChange = false; // �Ƿ������װ��

	private boolean bisappendagechg = false; // �Ƿ��������

	private boolean biscontractchg = false; // �Ƿ�ǩԼ���ϲ���

	private boolean fitmenterrFlag = false;	// װ�޿�ʵ���쳣

	private SoCourseBVO[] oldcourseBVOs = null;	// �Ϲ�תǩԼʱ�Ϲ�����

	private String pk_lastbill = "";// ��Դ��������

	private boolean btranstomem = false;	// �Ƿ�ͻ�����Ա

	private String[] pk_customers = null;//��Ҫ����Ա�Ŀͻ�

	private Map<String, SoCourseBVO[]> courseBVOSMap = new HashMap<String, SoCourseBVO[]>();

	/**
	 * ��Ч�� ����ͨ��״̬
	 */
	private final static int btnState_effectiveApprove = 11;

	/**
	 * ���ߣ����� ʱ�䣺2007-3-5 ����04:02:09
	 */
	public ClientUI() {
		super();
		init();
	}

	public ClientUI(Boolean billSource) {
		super(billSource);
		setLineableTblCodes(new String[]{ "ps_so_sign_customer",
				  "ps_so_sign_discount",
				  "ps_so_sign_fmcrchan",
				  "ps_so_appendage"});

	}

	private void init(){
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		setFirstPageSign("ps_so_sign_customer");
		setSecondPageSign("ps_cmg_csellor_record");
		setCoursePageSign("ps_so_course_b");
		setSubscPageDiscountName("ps_so_sign_discount");
		setDiscountVOName(SoSignDiscountVO.class.getName());
		setCoursePageIndex(2);
		setSecondPageIndex(1);
		setDiscountPageIndex(3);
		initCustRef();
//		getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, false);
		getBillCardPanel().getBillTable("ps_so_course_b").removeSortListener();
		setLineableTblCodes(new String[]{ "ps_so_sign_customer",
				  "ps_so_sign_discount",
				  "ps_so_sign_fmcrchan",
				  "ps_so_appendage"});
		try {
			//������չ״̬
			getButtonManager().setButtonByextendStatus(getExtendStatus(null));
			updateButtons();
		} catch (Exception e) {
			Logger.error("��ʼ����ť�����쳣��", e);
		}
	}

	@Override
	public Map<String, Set<String>> getBodySpecialPenetrateCode() {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		Set<String> set = new HashSet<String>();
		set.add("cusname");
		set.add("vcusname");
		map.put("ps_so_sign_customer", set);
		return map;
	}

	@Override
	public void onPenerate(String itemkey) {
		if("cusname".equals(itemkey)||"vcusname".equals(itemkey)){
			BillModel bm = isListPanelSelected()?getBillListPanel().getBodyBillModel("ps_so_sign_customer"):getBillCardPanel().getBillModel("ps_so_sign_customer");
			UITable ut = isListPanelSelected()?getBillListPanel().getBodyTable("ps_so_sign_customer"):getBillCardPanel().getBillTable("ps_so_sign_customer");
			CmgCustomerVO cvo = new CmgCustomerVO();
			cvo.setPrimaryKey((String)bm.getValueAt(ut.getSelectedRow(), "pk_customer"));
			String ca = (String)bm.getValueAt(ut.getSelectedRow(), "fccategory");
			if(ca!=null && IDownList.CUSTOMER_KIND[1].equals(ca))
				cvo.setFccategory(1);
			else
				cvo.setFccategory(0);
			new PenerateToCustomer().onPenerate(this, getModuleCode(), cvo);
		}
	}

	/**
	 * @�����ߣ�����
	 * @����˵����
	 * @����ʱ�䣺2007-3-14 ����11:53:56
	 * @�޸��ߣ�tm
	 * @�޸�ʱ�䣺2007-3-14 ����11:53:56
	 * @override
	 */
	@Override
	protected void initPrivateButton() {
		super.initPrivateButton();
		nc.vo.trade.button.ButtonVO lineBtn = (new LineBtnVO()).getButtonVO();
		lineBtn.setChildAry(new int[] { IBillButton.AddLine, IBillButton.InsLine, IBillButton.DelLine });

		addPrivateButton(lineBtn);

		nc.vo.trade.button.ButtonVO homeMadeBtn = (new HomeMadeBtn()).getButtonVO();
		nc.vo.trade.button.ButtonVO subscBtn = (new SoSubscButton()).getButtonVO();
		nc.vo.trade.button.ButtonVO queueBtn = (new QueueButton()).getButtonVO();
		nc.vo.trade.button.ButtonVO engageBtn = (new SoEngageButton()).getButtonVO();
		nc.vo.trade.button.ButtonVO addBtn = (new AddNewBtn()).getButtonVO();
		nc.vo.trade.button.ButtonVO processBtn = (new CreateCourseBtn()).getButtonVO();
		nc.vo.trade.button.ButtonVO receiveBtn = (new ReceiveBtn()).getButtonVO();
		nc.vo.trade.button.ButtonVO discountBtn = (new DiscountBtn()).getButtonVO();
		nc.vo.trade.button.ButtonVO changeDiscountBtn = (new ChangeDiscountBtn()).getButtonVO();

		addBtn.setChildAry(new int[] { IPSButton.HomeMadeBtn, IPSButton.QueueButton, IPSButton.SoEngageBtn, IPSButton.SoSubscBtn });
		addBtn.setOperateStatus(new int[] { IBillOperate.OP_INIT, IBillOperate.OP_NOADD_NOTEDIT, IBillOperate.OP_NOTEDIT });
		/*
		 * nc.vo.trade.button.ButtonVO vindicateBtn = (new ClientVindicate()).getButtonVO(); nc.vo.trade.button.ButtonVO
		 * privateBtn = (new PrivateClient()).getButtonVO(); nc.vo.trade.button.ButtonVO enterpriseBtn = (new
		 * EnterpriseClient()).getButtonVO(); vindicateBtn.setChildAry(new int[]{ IPSButton.PrivateClient,
		 * IPSButton.EnterpriseClient }); vindicateBtn.setOperateStatus(new
		 * int[]{IBillOperate.OP_ADD,IBillOperate.OP_REFADD}); privateBtn.setOperateStatus(new
		 * int[]{IBillOperate.OP_ADD,IBillOperate.OP_REFADD}); enterpriseBtn.setOperateStatus(new
		 * int[]{IBillOperate.OP_ADD,IBillOperate.OP_REFADD}); addPrivateButton(vindicateBtn);
		 * addPrivateButton(privateBtn); addPrivateButton(enterpriseBtn);
		 */

		addBtn.setOperateStatus(new int[] { IBillOperate.OP_NOTEDIT, IBillOperate.OP_INIT });
		receiveBtn.setOperateStatus(new int[] { IBillOperate.OP_NOTEDIT });
		processBtn.setOperateStatus(new int[] { IBillOperate.OP_ADD, IBillOperate.OP_REFADD, IBillOperate.OP_EDIT });
		discountBtn.setOperateStatus(new int[] { IBillOperate.OP_ADD, IBillOperate.OP_REFADD, IBillOperate.OP_EDIT });
//		changeDiscountBtn.setOperateStatus(new int[] { IBillOperate.OP_NOADD_NOTEDIT });
//		changeDiscountBtn.setBusinessStatus(new int[] { IBillStatus.CHECKPASS });
		changeDiscountBtn.setExtendStatus(new int[]{btnState_effectiveApprove});
//		receiveBtn.setOperateStatus(new int[] { IBillOperate.OP_NOADD_NOTEDIT });
//		receiveBtn.setBusinessStatus(new int[] { IBillStatus.CHECKPASS });
		receiveBtn.setExtendStatus(new int[]{btnState_effectiveApprove});

		addPrivateButton(addBtn);
		addPrivateButton(homeMadeBtn);
		addPrivateButton(subscBtn);
		addPrivateButton(queueBtn);
		addPrivateButton(engageBtn);
		addPrivateButton(processBtn);
		addPrivateButton(receiveBtn);
		addPrivateButton(discountBtn);
		addPrivateButton(changeDiscountBtn);

		AppendageChange appendagechgBtn = new AppendageChange(); // ���������
//		appendagechgBtn.getButtonVO().setOperateStatus(new int[] { -1 });
//		appendagechgBtn.getButtonVO().setBusinessStatus(new int[] { IBillStatus.CHECKPASS });
		appendagechgBtn.getButtonVO().setExtendStatus(new int[]{btnState_effectiveApprove});
		addPrivateButton(appendagechgBtn.getButtonVO());

		ChangeContractBtn changeContractBtn = new ChangeContractBtn(); // ǩԼ���ϲ���
//		changeContractBtn.getButtonVO().setOperateStatus(new int[] { -1 });
//		changeContractBtn.getButtonVO().setBusinessStatus(new int[] { IBillStatus.CHECKPASS });
		changeContractBtn.getButtonVO().setExtendStatus(new int[]{btnState_effectiveApprove});
		addPrivateButton(changeContractBtn.getButtonVO());

		ContractAuditBtn contractAuditBtn = new ContractAuditBtn(); // �������
//		contractAuditBtn.getButtonVO().setOperateStatus(new int[] { -1 });
//		contractAuditBtn.getButtonVO().setBusinessStatus(new int[] { IBillStatus.CHECKPASS });
		contractAuditBtn.getButtonVO().setExtendStatus(new int[]{btnState_effectiveApprove});
		addPrivateButton(contractAuditBtn.getButtonVO());
	}

	private void initCustRef() {
		UIRefPane refPanel = (UIRefPane) getBillCardWrapper().getBillCardPanel().getBillModel("ps_so_sign_customer").getItemByKey("cusname").getComponent();
		refPanel.setRefUICreator(new CustRefUICreator(this));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update(Observable o, Object arg) {
		super.update(o, arg);


		//add by chixy �������ݸ���ʱ��Ҫ����ҳǩ�ı༭��
		try {
			String fpmtype = null;
			if(!isListPanelSelected()){
				fpmtype = getBillCardPanel().getHeadItem("fpmtype").getValue();

			}else{
				fpmtype = (String)getBillListPanel().getHeadBillModel().getValueAt(getBillListPanel().getHeadTable().getSelectedRow(), "fpmtype");
			}
			setMortgageValues(false, fpmtype);

		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		//add by chixy 2010-05-15 ע���ֶ�Ĭ����ʾ��Ϣ����
		BillItem extrItem = getBillCardPanel().getHeadItem("extratext");
		if(extrItem !=null){
			extrItem.setValue(extrItem.getDefaultValueObject());
		}
		//end by chixy
		if (!isListPanelSelected()){
			updateywBtnState();
		}else{
			updateywBtnState();
		}



	}
	@SuppressWarnings("deprecation")
	public void updateywBtnState() {

//		if (!isListPanelSelected()) {
//			if (getBillOperate() == IBillOperate.OP_NOADD_NOTEDIT
//					|| getBillOperate() == IBillOperate.OP_NOTEDIT
//					|| getBillOperate() == IBillOperate.OP_INIT) {
//				String reserve1 = getBillCardPanel().getHeadItem("reserve1")
//						.getValue() == null ? null : getBillCardPanel()
//						.getHeadItem("reserve1").getValue().toString();
//				if (reserve1 == null || reserve1.trim().length() == 0) {
//					try {
//						updateButtonUI();
//					} catch (Exception e) {
//						Logger.error(e.getMessage(), e);
//					}
//					return;
//				}
//				if (reserve1 != null && reserve1.trim().length() > 0) {
//					// �տŤ�Ŀ���
//					ButtonObject bogb = getButtonManager().getButton(
//							IPSButton.ReceiveBtn);
//					if (bogb == null)
//						return;
//					bogb.setEnabled(false);
//					// �������������
//					ButtonObject boac = getButtonManager().getButton(
//							IPSButton.AppendageChgBtn);
//					if (boac == null)
//						return;
//					boac.setEnabled(false);
//					// �ļ�����
//					ButtonObject bof = getButtonManager().getButton(
//							IBillButton.File);
//					if (bof == null)
//						return;
//					bof.setEnabled(false);
//					// ִ�а�ť
//					ButtonObject bod = getButtonManager().getButton(
//							IBillButton.Action);
//					if (bod == null)
//						return;
//					bod.setEnabled(false);
//					// ������ť
//					ButtonObject boass = getButtonManager().getButton(
//							IBillButton.Ass);
//					if (boass == null)
//						return;
//					boass.setEnabled(false);
//					// ����ۿ۰�ť
//					ButtonObject bocdb = getButtonManager().getButton(
//							IPSButton.ChangeDiscountBtn);
//					if (bocdb == null)
//						return;
//					bocdb.setEnabled(false);
//					try {
//						updateButtonUI();
//					} catch (Exception e) {
//						Logger.error(e.getMessage(), e);
//					}
//				}
//			}
//		} else {
//			if (getBillOperate() == IBillOperate.OP_NOADD_NOTEDIT
//					|| getBillOperate() == IBillOperate.OP_NOTEDIT
//					|| getBillOperate() == IBillOperate.OP_INIT) {
//				// getButtonManager().getButton(IBillButton.Line).setEnabled(false);
//				int rowIndex = getBillListPanel().getHeadTable()
//						.getSelectedRow();
//				if (rowIndex < 0) {
//					rowIndex = 0;
//				}
//				try {
//					String reserve1 = getBillListPanel().getHeadBillModel()
//							.getValueAt(rowIndex, "reserve1") == null ? null
//							: getBillListPanel().getHeadBillModel().getValueAt(
//									rowIndex, "reserve1").toString();
//					if (reserve1 == null || reserve1.trim().length() == 0) {
//						try {
//							updateButtonUI();
//						} catch (Exception e) {
//							Logger.error(e.getMessage(), e);
//						}
//						return;
//					}
//					if (reserve1 != null && reserve1.trim().length() > 0) {
//						// �տŤ�Ŀ���
//						ButtonObject bogb = getButtonManager().getButton(
//								IPSButton.ReceiveBtn);
//						if (bogb == null)
//							return;
//						bogb.setEnabled(false);
//						// �������������
//						ButtonObject boac = getButtonManager().getButton(
//								IPSButton.AppendageChgBtn);
//						if (boac == null)
//							return;
//						boac.setEnabled(false);
//						// �ļ�����
//						ButtonObject bof = getButtonManager().getButton(
//								IBillButton.File);
//						if (bof == null)
//							return;
//						bof.setEnabled(false);
//						// ִ�а�ť
//						ButtonObject bod = getButtonManager().getButton(
//								IBillButton.Action);
//						if (bod == null)
//							return;
//						bod.setEnabled(false);
//						// ������ť
//						ButtonObject boass = getButtonManager().getButton(
//								IBillButton.Ass);
//						if (boass == null)
//							return;
//						boass.setEnabled(false);
//						// ����ۿ�
//						ButtonObject boass1 = getButtonManager().getButton(
//								IPSButton.ChangeDiscountBtn);
//						if (boass1 == null)
//							return;
//						boass1.setEnabled(false);
//						try {
//							updateButtonUI();
//						} catch (Exception e) {
//							Logger.error(e.getMessage(), e);
//						}
//					}
//
//				} catch (Exception e) {
//				}
//
//			}
//		}
	}

	@SuppressWarnings("deprecation")
	protected void setHeadEditItem() {
		if (getBillOperate() != IBillOperate.OP_EDIT && getBillOperate() != IBillOperate.OP_REFADD && getBillOperate() != IBillOperate.OP_ADD)
			return;
		if (getBillCardPanel().getHeadItem("pk_house").getValue() != null && getBillCardPanel().getHeadItem("pk_house").getValue().toString().length() > 0) {
			if (getBillCardPanel().getHeadItem("vbillstatus").getValueObject() != null
					&& Integer.parseInt(getBillCardPanel().getHeadItem("vbillstatus").getValueObject().toString()) == 1) {
				getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
			} else {
				Object pk_sign = getBillCardPanel().getHeadItem("pk_sign").getValueObject();
				if (pk_sign != null && pk_sign.toString().length() > 0) {
					// ����ǩԼ����տ
					CbGatheringVO[] cbVO = null;
					try {
						cbVO = (CbGatheringVO[]) new PSBusinessDelegator().queryByCondition(CbGatheringVO.class, "pk_sourcebill='" + pk_sign.toString() + "'",
								null, null, new String[] { "pk_sourcebill" });
					} catch (BusinessException e) {
					}
					if(cbVO!=null && cbVO.length>0){
						getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
					}else{
						getBillCardPanel().getHeadItem("pk_paymode").setEnabled(true);
					}
				}else{
					getBillCardPanel().getHeadItem("pk_paymode").setEnabled(true);
				}
			}

			if (!getBisappendagechg() && !isBiscontractchg()){
				if (getBillCardPanel().getHeadItem("vbillstatus").getValueObject() != null
						&& Integer.parseInt(getBillCardPanel().getHeadItem("vbillstatus").getValueObject().toString()) == 1) {
					getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(false);
				}else{
					getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(true);
				}
			}
		} else {
			getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
			getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(false);
			getBillCardPanel().getHeadItem("pk_paymode").setValue(null);
			getBillCardPanel().getHeadItem("pk_fmcrit").setValue(null);
		}


		/*
		 * Object lastBill = getBillCardPanel().getHeadItem("vlastbill").getValueObject(); if(lastBill!=null &&
		 * lastBill.toString().length()>0){ getBillCardPanel().getHeadItem("nyprojectmny").setEdit(false); }else{
		 * getBillCardPanel().getHeadItem("nyprojectmny").setEdit(true); }
		 */
		// getBillCardPanel().execHeadFormulas(new
		// String[]{"dputhousedate->getColValue(crm_bd_building, dlivingdate,
		// pk_building, pk_building)"});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void afterEdit(BillEditEvent e) {
		if (e.getPos() == 1 && e.getTableCode().trim().equals(getSubscPageDiscountName())) {
			setChange(true);
		}
		if (e.getKey().equalsIgnoreCase("cusname") && e.getPos() == 1 && e.getTableCode().equalsIgnoreCase("ps_so_sign_customer")) {
			getBillCardPanel().getBillModel("ps_so_sign_customer").execLoadFormula();
		}
		super.afterEdit(e);
		updateUI();
		if (e.getKey().equalsIgnoreCase("pk_paymode")) {
			setPayModeChange(true);
			//**********************2010-5-17 ½�� �༭��ʽ�Ѱ������������ִ��**************************
//			getBillCardPanel().execHeadFormula("fchoice->getColValue(ps_bd_paymode,fchoice,pk_paymode,pk_paymode);");
			//**********************2010-5-17 ½�� �༭��ʽ�Ѱ������������ִ��**************************
			try {
				((ClientEventHandler) getManageEventHandler()).handlingPrice(getBillCardPanel().getHeadItem("nypricebeforedis").getValueObject(),
						getBillCardPanel().getHeadItem("nytotalbeforedis").getValueObject());

				// ���ð���������Ϣ
				setMortgageValues(true,null);
			} catch (Exception e1) {
				nc.bs.logging.Logger.error(e1.getMessage(), e1);
			}
		}
		PSBusinessDelegator delegator = new PSBusinessDelegator();
		if (e.getPos() == 0) {//modify by wangjiang at 20100512
			//super.afterEdit��Ŷ�pk_currtype�༭�¼����д���
			//�Ҵ�������Ժ�ִ��execHeadLoadFormulas��Ŀ���ǽ��µĻ�����Ч����ˣ�
			//2010-5-17 ½�� pk_currtype�ı༭���¼����������ڣ���������Ż���Ҫ�жϺδ�����pk_currtype�ĸı䣬�Ӷ��������´��룬��ʱ�Ȼָ�ȫ��ִ��
//			if(e.getKey().trim().equals("pk_currtype")){
			//2010-5-17 ½�� pk_currtype�ı༭���¼����������ڣ���������Ż���Ҫ�жϺδ�����pk_currtype�ĸı䣬�Ӷ��������´��룬��ʱ�Ȼָ�ȫ��ִ��
			if (getBillCardPanel().getHeadItem("pk_currtype").getValueObject() != null)
				afterCurrencyEdit(getBillCardPanel().getHeadItem("pk_currtype").getValue());
//			}
			setHeadEditItem();
			if (e.getKey().trim().indexOf("pk_house") == 0) {
				if (e.getValue() != null && e.getValue().toString().length() > 0)
					getBillCardPanel().getHeadItem("bisdmltn").setEnabled(true);
				else {
					getBillCardPanel().setHeadItem("bisdmltn", null);
					getBillCardPanel().getHeadItem("bisdmltn").setEnabled(false);
					getBillCardPanel().setHeadItem("pk_dmltnsite", null);
					getBillCardPanel().getHeadItem("pk_dmltnsite").setEnabled(false);
				}

				/**
				try {
					getBillCardPanel().setHeadItem("vbillno",getBillNo());
				} catch (Exception e1) {

				}
				// ��ͬ��
				getBillCardPanel().setHeadItem("vbargainnum", getBillCardPanel().getHeadItem("vbillno").getValueObject());
				*/
				getBillCardPanel().getBillModel("ps_so_appendage").clearBodyData();//������ҳǩ��ձ���

				if(SafeObject.isNull(getBillCardPanel().getHeadItem("pk_lastbill").getValueObject())&&
						SafeObject.isNull(getHeadItemString("pk_house"))){
					getBillCardPanel().getBillModel("ps_so_sign_customer").clearBodyData();//���ķ�������տͻ�ҳǩ
					getBillCardPanel().getBillModel("ps_so_sign_customer").addLine();
				}

				//**********************2010-5-17 ½�� �༭��ʽ�Ѱ������������ִ��**************************
//				execHeadFormulas(new String[] { "pk_price->getColValue(crm_bd_price, pk_price, pk_house, pk_house);",
//						"sellarea->getColValue(crm_bd_house, nsellarea, pk_house, pk_house);", "nsignarea->sellarea;",
//						"nypricebeforedis->getColValue(crm_bd_price, nyprice, pk_price, pk_price);",
//						"nytotalbeforedis->getColValue(crm_bd_price, nymny, pk_price, pk_price);",
//						"dputhousedate->getColValue(crm_bd_building, dlivingdate, pk_building, pk_building);" });
				//**********************2010-5-17 ½�� �༭��ʽ�Ѱ������������ִ��**************************
				
				getBillCardPanel().getHeadItem("pk_paymode").setValue(null);
				if(PsCommonUtil.notNull(getHeadItemString("pk_house"))){
					getBillCardPanel().getHeadItem("pk_paymode").setEnabled(true);
				}else{
					getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
				}
			
			}
			if (e.getKey().equalsIgnoreCase("bisdmltn")){
				setPayModeChange(true);
				changeEditStatus();
			}

			if (e.getKey().equalsIgnoreCase("pk_dmltnsite") || e.getKey().equalsIgnoreCase("pk_house")){
				setPayModeChange(true);
				if (getBillCardPanel().getHeadItem("pk_dmltnsite").getValueObject()!=null){
					afterHouseDmltnsiteEdit(getBillCardPanel().getHeadItem("pk_dmltnsite").getValueObject().toString(),getBillCardPanel().getHeadItem("pk_house").getValueObject().toString());
				}
				else if (e.getKey().equalsIgnoreCase("pk_dmltnsite")){
					getHousePrice();
				}
			}

			if (e.getKey().trim().indexOf("nypprice") == 0 || e.getKey().trim().indexOf("ncurrrate") == 0 || e.getKey().trim().indexOf("pk_project") == 0)
				getBillCardPanel().execHeadLoadFormulas();
			/*
			 * if(e.getKey().trim().indexOf("pk_currtype")==0){
			 * afterCurrencyEdit(getBillCardPanel().getHeadItem("pk_currtype").getValue()); }
			 */

			// ѡ�й��̿�ַ���󣬹�Ӧ�̡��ַ�����ַ���ԭ���ֶοɱ༭
			if (e.getKey().trim().indexOf("bactmny") == 0) {
				if (getBillCardPanel().getHeadItem("bactmny").getValue().equals("true")) {
					getBillCardPanel().getHeadItem("pk_provider").setEnabled(true);
					getBillCardPanel().getHeadItem("nyprojectmny").setEnabled(true);
					getBillCardPanel().getHeadItem("vprojectreason").setEnabled(true);
					getBillCardPanel().getBillModel("ps_so_sign_customer").setEnabled(false);
				} else {
					getBillCardPanel().getHeadItem("pk_provider").setEnabled(false);
					getBillCardPanel().getHeadItem("nyprojectmny").setEnabled(false);
					getBillCardPanel().getHeadItem("vprojectreason").setEnabled(false);
					getBillCardPanel().setHeadItem("pk_provider", null);
					getBillCardPanel().setHeadItem("nyprojectmny", null);
					getBillCardPanel().setHeadItem("vprojectreason", null);

					getBillCardPanel().getBillModel("ps_so_sign_customer").setEnabled(true);
				}
			}
			// ѡ��Ӧ�̺��Զ�����Ӧ�̴������
			if (e.getKey().trim().indexOf("pk_provider") == 0) {
				if (getBillCardPanel().getHeadItem("pk_provider").getValue() == null)
					return;
				try {
					String pk_manprovider = getBillCardPanel().getHeadItem("pk_provider").getValue();
					CumandocVO mandocVO = (CumandocVO) delegator.queryByPrimaryKey(CumandocVO.class, pk_manprovider);
					CubasdocVO basdocVO = (CubasdocVO) delegator.queryByPrimaryKey(CubasdocVO.class, mandocVO.getPk_cubasdoc());
					String pk_cubasdoc = basdocVO.getPrimaryKey();
					getBillCardPanel().getBillModel("ps_so_sign_customer").clearBodyData();
					getBillCardPanel().getBillModel("ps_so_sign_customer").addLine();
					getBillCardPanel().getBillModel("ps_so_sign_customer").setValueAt(pk_cubasdoc, 0, "pk_customer");
					getBillCardPanel().getBillModel("ps_so_sign_customer").setValueAt(100, 0, "nproperty");
					getBillCardPanel().getBillModel("ps_so_sign_customer").execFormulas(
							new String[] { "cusname->getColValue(bd_cubasdoc, custname, pk_cubasdoc, pk_customer);",
									"vccode->getColValue(bd_cubasdoc, custcode, pk_cubasdoc, pk_customer)" });
					String cusname = (String) getBillCardPanel().getBillModel("ps_so_sign_customer").getValueAt(0, "cusname");
					if(cusname!=null){
						getBillCardPanel().getBillModel("ps_so_sign_customer").setValueAt(
								StringUtil.getPYIndexStr(cusname.toString().replaceAll(" ", ""), true),0,"vpcnamespell");
					}
					getBillCardPanel().getBillModel("ps_so_sign_customer").setValueAt(IPsDataMapping.CUSTOMER_SIGN, 0, "fcustype");
					getBillCardPanel().getBillModel("ps_so_sign_customer").setValueAt(0, 0, "fccategory");
					getBillCardPanel().getBillModel("ps_so_sign_customer").setValueAt(BillDateGetter.getBillDate(), 0, "dregdate");
					return;
				} catch (Exception exc) {
					return;
				}
			}
			// ����װ�޵��ۺ������¼���Ƿ����װ�޿�
			if (e.getKey().trim().indexOf("nyfmprice") == 0) {
				String nyfmprice = getBillCardPanel().getHeadItem("nyfmprice").getValue() == null ? null : getBillCardPanel().getHeadItem("nyfmprice")
						.getValue().toString();
				if (nyfmprice == null || nyfmprice.trim().length() == 0) {
					getBillCardPanel().getHeadItem("bisfmcrit").setEnabled(false);
					getBillCardPanel().getHeadItem("bisfmcrit").setValue(null);
				} else {
					getBillCardPanel().getHeadItem("bisfmcrit").setEnabled(true);
				}
			}
			if (e.getKey().trim().indexOf("pk_fmcrit") == 0) {
				setFitmentChange(true);
				int irow = getBillCardPanel().getBillModel("ps_so_sign_discount").getRowCount();
				if (irow > 0) {
					for (int i = 0; i < irow; i++) {
						getBillCardPanel().getBillModel("ps_so_sign_discount").setValueAt(true, i, "reserve15");
					}
				}

				if (e.getValue() != null && e.getValue().toString().length() > 0) {
					getBillCardPanel().execHeadFormulas(
							new String[] { "nyfmprice->getColValue(ps_bd_fitmemtdoc, nprice, pk_fitmemtdoc, pk_fmcrit);", "nftundismny->nyfmprice*nsignarea;",
									"nftdismny->nyfmprice*nsignarea;" });
				}
				// ����װ�ޱ���
				try {
					generateFitment();
					// setHeadFitment();
				} catch (Exception e1) {
					nc.bs.logging.Logger.error(e1.getMessage(), e1);
				}
			}

			//add by chixy 2010-09-21 �Ϲ����ڱ༭ʱ��������������Ҫͬ��
			if (e.getKey().equalsIgnoreCase("dsigndate")){
				Object date = getBillCardPanel().getHeadItem("dsigndate").getValueObject();
				getBillCardPanel().getHeadItem("despchgdate").setValue(date);
			}

		}
		if (getBillCardPanel().getHeadItem("vlastbill").getValueObject() != null
				&& getBillCardPanel().getHeadItem("vlastbill").getValueObject().toString().equals("946D")) {
//			getBillCardPanel().getHeadItem("pk_paymode").setEdit(false);
			getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(false);
			getBillCardPanel().getHeadItem("bisdmltn").setEnabled(false);
			getBillCardPanel().getHeadItem("pk_dmltnsite").setEnabled(false);
		}
		if (e.getPos() == 1 && e.getTableCode().equals("ps_so_course_b") && e.getKey().trim().indexOf("bisfund") == 0) {
			editCourse(e);
		}

		if (e.getPos() == 0 && e.getKey().trim().equals("bispresent")) {
			// �ж��Ƿ��и�����
			int irow = getBillCardPanel().getBillModel("ps_so_appendage").getRowCount();
			if (irow > 0) {
				getBillCardPanel().setHeadItem("bispresent", new UFBoolean(false));
				MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001053")//@res "�Ѵ��ڸ���������������!"
);
				return;
			}

			// �Ƿ�����״̬�ı����ո��ʽ
			getBillCardPanel().getHeadItem("pk_paymode").setValue("");
		}

		if (e.getPos() == 1) {
			if (e.getKey().trim().equals("vhname")) {
//				getBillCardPanel().getBillModel("ps_so_appendage")
//						.execFormulas(
//								e.getRow(),
//								new String[] { "nyprice->getColValue(crm_bd_price,nyprice ,pk_house ,pk_house );",
//										"nymny->getColValue(crm_bd_price, nymny, pk_house, pk_house);",
//										"nprice->getColValue(crm_bd_price,nprice ,pk_house ,pk_house );",
//										"nmny->getColValue(crm_bd_price, nmny, pk_house, pk_house);"
//										});
				getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(getBillCardPanel().getHeadItem("pk_currtype").getValueObject().toString(),
						e.getRow(), "pk_currtype");
				getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(getBillCardPanel().getHeadItem("ncurrrate").getValueObject().toString(),
						e.getRow(), "ncurrrate");
				if (getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "pk_house") != null) {
					@SuppressWarnings("unused")
					String pk_house = getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "pk_house").toString();
//					BdHouseVO houseVO = getHouseVOByPrimarykey(pk_house);
//
//					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(houseVO.getFsellmethod(), e.getRow(), "fsellmethod");
//					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(houseVO.getNbuildarea(), e.getRow(), "nbuildarea");
//					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(houseVO.getNinarea(), e.getRow(), "ninarea");
//					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(houseVO.getNsellarea(), e.getRow(), "nsellarea");

					String pk_sellstate = (String)getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "pk_sellstate");
					HouseStatesMapping stateMap = HouseStatesMapping.createHouseStatesMapping();
					ArrayList<String> al = new ArrayList<String>();
					for (int i = 0; i < ISellStatus.M_BESALE.length; i++) {
						al.add(stateMap.getTempSellStatesR().get(ISellStatus.M_BESALE[i]).toString());
					}
					if (al.contains(pk_sellstate)) {
						getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(new UFBoolean(true), e.getRow(), "bissingle");
					} else {
						getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(new UFBoolean(false), e.getRow(), "bissingle");
					}
				}
			}

			// NCdp201946365 2010-5-27 ½�� ��������´���bug����������ж�ֵ�Ƿ��иı�
			if (e.getKey().equals("biscombined")
					&& ((e.getOldValue() != null &&!e.getOldValue().equals(e.getValue())) || e.getOldValue() == null)) {
				UFBoolean biscombined = new UFBoolean(getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "biscombined").toString());
//				UFDouble nypricebeforedis = new UFDouble(getBillCardPanel().getHeadItem("nypricebeforedis").getValueObject().toString());
//				UFDouble nytotalbeforedis = new UFDouble(getBillCardPanel().getHeadItem("nytotalbeforedis").getValueObject().toString());
//				UFDouble nsalearea = new UFDouble(getBillCardPanel().getHeadItem("nsignarea").getValueObject().toString());
//				UFDouble nymny = new UFDouble(getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "nymny").toString());
//				if (biscombined.booleanValue()) {
//					nytotalbeforedis = SafeCompute.add(nytotalbeforedis, nymny);
//					nypricebeforedis = SafeCompute.div(nytotalbeforedis, nsalearea);
//				} else {
//					nytotalbeforedis = SafeCompute.sub(nytotalbeforedis, nymny);
//					nypricebeforedis = SafeCompute.div(nytotalbeforedis, nsalearea);
//				}
				//getBillCardPanel().getHeadItem("nypricebeforedis").setValue(nypricebeforedis);
				//getBillCardPanel().getHeadItem("nytotalbeforedis").setValue(nytotalbeforedis);
				//add by chixy 2010-09-08 ��չ��ۺ���
				if(!biscombined.booleanValue()){
					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(null,e.getRow(), "nydisprice");
					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(null,e.getRow(), "ndisprice");
					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(null,e.getRow(), "nydismny");
					getBillCardPanel().getBillModel("ps_so_appendage").setValueAt(null,e.getRow(), "ndismny");
				}
				setChange(true);
			}

			if (e.getKey().trim().equals("nyshouldmny")) {
				modifyShouldMny(e);
				//add by chixy ����ǩԼ������޸����̬���̵�Ӧ�ս���Ҫ����Ӧ�պ����·���ʵ��
				SoCourseBVO[] courseBVOs = getCourseCacheVO();
				UFDouble mny = ((ClientEventHandler)getManageEventHandler()).getCourseReceiveMny(courseBVOs);
				try {
					((ClientEventHandler)getManageEventHandler()).distributeCourseMny(mny, courseBVOs);
				//end by chixy
				} catch (Exception e2) {
					Logger.error(e2.getStackTrace(), e2);
					showHintMessage(e2.getMessage());
				}
			}
			if (e.getKey().trim().equals("vdbcode") || e.getKey().trim().equals("fpriority")) {

				if (e.getKey().trim().equals("fpriority")) {
					if(!checkDisProtyUnique(getSubscPageDiscountName(), e.getRow())){
						showErrorMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001054")//@res "�ۿ����ȼ������ظ���������ѡ��"
);
						return;
					}
				}
				// guhm
				// 2009-1-20
				// ǩԼ�ۿ��ӱ�-�ۿ۶���
				getBillCardPanel()
						.execBodyFormulas(
								e.getRow(),
								new String[] { "discountobject->getColValue(ps_prm_discount,discountobject,pk_discount,getColValue(ps_prm_discount_b,pk_discount,pk_discount_b ,pk_discount ));" });
				if (getBillCardPanel().getBodyValueAt(e.getRow(), "discountobject") != null) {
					String discountobject = getBillCardPanel().getBodyValueAt(e.getRow(), "discountobject").toString();
					if (discountobject != null && discountobject.length() > 0) {
						if (discountobject.equals("0")) {
							getBillCardPanel().setBodyValueAt(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001089")//@res "�����λ�װ�޿�"
, e.getRow(), "discountobject");
						} else if (discountobject.equals("1")) {
							getBillCardPanel().setBodyValueAt(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001056")//@res "�����λ��"
, e.getRow(), "discountobject");
						} else if (discountobject.equals("2")) {
							getBillCardPanel().setBodyValueAt(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000775")//@res "װ�޿�"
, e.getRow(), "discountobject");
						}

					}
				}
				// ����ۿ�ʱ��ʼ�������ۿ۶�
				if (e.getKey().trim().equals("vdbcode"))
					getBillCardPanel().setBodyValueAt("0.00", e.getRow(), "nythisdiscountmny");
				if (getBillCardPanel().getBodyValueAt(e.getRow(), "bisrefmodify") != null) {
					Boolean canModify = (Boolean) getBillCardPanel().getBodyValueAt(e.getRow(), "bisrefmodify");
					if (canModify) {
						getBillCardPanel().setCellEditable(e.getRow(), "nythisdiscountmny", true);
						getBillCardPanel().setCellEditable(e.getRow(), "nyfitmentdiscountmny", true);
					} else {
						getBillCardPanel().setCellEditable(e.getRow(), "nythisdiscountmny", false);
						getBillCardPanel().setCellEditable(e.getRow(), "nyfitmentdiscountmny", false);
					}
					getBillCardPanel().setBodyValueAt(true, e.getRow(), "reserve14");
					getBillCardPanel().setBodyValueAt(true, e.getRow(), "reserve15");
					try {
						((ClientEventHandler) getManageEventHandler()).handlingPrice(getBillCardPanel().getHeadItem("nypricebeforedis").getValueObject(),
								getBillCardPanel().getHeadItem("nytotalbeforedis").getValueObject());
					} catch(BusinessException be){
						Logger.error(be.getMessage(), be);
						MessageDialog.showErrorDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, be.getMessage());
					}catch (Exception e1) {
						nc.bs.logging.Logger.error(e1.getMessage(), e1);
					}
				}
			}
			if (e.getKey().trim().equals("nythisdiscountmny")) {
				if (e.getOldValue() != null) {
					if (!e.getValue().toString().equals(e.getOldValue().toString())) {
						getBillCardPanel().setBodyValueAt(false, e.getRow(), "reserve14");
					}
				}
				try {
					if (getBillCardPanel().getHeadItem("nypricebeforedis") != null && getBillCardPanel().getHeadItem("nytotalbeforedis") != null)
						((ClientEventHandler) getManageEventHandler()).handlingPrice(getBillCardPanel().getHeadItem("nypricebeforedis").getValueObject(),
								getBillCardPanel().getHeadItem("nytotalbeforedis").getValueObject());
				} catch (Exception e1) {
					nc.bs.logging.Logger.error(e1.getMessage(), e1);
				}
			}
			if (e.getKey().trim().equals("nyfitmentdiscountmny")) {
				if (e.getOldValue() != null) {
					if (!e.getValue().toString().equals(e.getOldValue().toString())) {
						getBillCardPanel().setBodyValueAt(false, e.getRow(), "reserve15");
					}
				}
				try {
					if (getBillCardPanel().getHeadItem("nftundismny") != null && getBillCardPanel().getHeadItem("nftundismny") != null)
						((ClientEventHandler) getManageEventHandler()).handlingPrice(getBillCardPanel().getHeadItem("nypricebeforedis").getValueObject(),
								getBillCardPanel().getHeadItem("nytotalbeforedis").getValueObject());
				} catch (Exception e1) {
					nc.bs.logging.Logger.error(e1.getMessage(), e1);
				}
			}
			if (e.getKey().trim().equals("vcode")) {
				// ����װ���ӱ���в���ʱ�����ºϼ�װ�޿��
				setHeadFitment();
			}
		}
//		getBillCardPanel().execHeadLoadFormulas();
//		getBillListWrapper().getBillListPanel().getBillListData().getHeadBillModel().execLoadFormula();
	}


	private void changeEditStatus(){
		UFBoolean bisdmltn = new UFBoolean(getBillCardPanel().getHeadItem("bisdmltn").getValueObject().toString());
		if (bisdmltn != null){
			if (!bisdmltn.booleanValue()){
				getBillCardPanel().getHeadItem("pk_dmltnsite").setValue(null);
				getHousePrice();
			}
			getBillCardPanel().getHeadItem("pk_dmltnsite").setEnabled(bisdmltn.booleanValue());
			//getBillCardPanel().getHeadItem("nypricebeforedis").setEnabled(!bisdmltn.booleanValue());
		}
	}

	private void addAppendagePrice(){
		UFDouble nypricebeforedis = new UFDouble(getBillCardPanel().getHeadItem("nypricebeforedis").getValueObject().toString());
		UFDouble nytotalbeforedis = new UFDouble(getBillCardPanel().getHeadItem("nytotalbeforedis").getValueObject().toString());
		UFDouble nsalearea = new UFDouble(getBillCardPanel().getHeadItem("nsignarea").getValueObject().toString());
		SoAppendageVO[] vos = (SoAppendageVO[])getBillCardPanel().getBillModel("ps_so_appendage").getBodyValueVOs(SoAppendageVO.class.getName());
		if (vos != null && vos.length > 0){
			for (int i=0;i<vos.length;i++){
				if (vos[i].getBiscombined() != null && vos[i].getBiscombined().booleanValue()){
					nytotalbeforedis = SafeCompute.add(nytotalbeforedis, vos[i].getNymny());
				}
			}
			nypricebeforedis = SafeCompute.div(nytotalbeforedis, nsalearea);
		}
		getBillCardPanel().getHeadItem("nypricebeforedis").setValue(nypricebeforedis);
		getBillCardPanel().getHeadItem("nytotalbeforedis").setValue(nytotalbeforedis);
		setChange(true);
	}

	private void getHousePrice(){
		getBillCardPanel().execHeadFormulas(
				new String[] { "nypricebeforedis->getColValue(crm_bd_price, nyprice, pk_price, pk_price);",
						"nytotalbeforedis->getColValue(crm_bd_price, nymny, pk_price, pk_price);" });
		addAppendagePrice();
	}

	private void afterHouseDmltnsiteEdit(String pk_dmltnsite, String pk_house){
		try {
			SuperVO[] parents = getBusiDelegator().queryByCondition(SoDmltnprcprgVO.class
					, " isnull(dr,0)=0 and isnull(biseffect,'N')='Y' and vbillstatus = '" + IBillStatus.CHECKPASS + "' and pk_building in (select pk_building from crm_bd_house where pk_house='" + pk_house + "')");
			if (parents == null || parents.length ==0){
				getHousePrice();
				MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001055")//@res "�Ҳ�����Ӧ�Ĳ�Ǩ�۸񷽰���"
);
				return;
			}
			String pk_dmltnprcprg = parents[0].getPrimaryKey();
			String year = parents[0].getAttributeValue("year")==null?"":parents[0].getAttributeValue("year").toString();
			String tempyear = "";

			for (int i=1;i<parents.length;i++){
				tempyear = parents[i].getAttributeValue("year")==null?"":parents[i].getAttributeValue("year").toString();
				if (tempyear.compareTo(year) > 0){
					pk_dmltnprcprg = parents[i].getPrimaryKey();
				}
			}

			SuperVO[] result = getBusiDelegator().queryByCondition(SoDmltnprcprgBVO.class
					, " isnull(dr,0)=0 and pk_dmltnsite='" + pk_dmltnsite + "'" +
					  " and pk_house = '" + pk_house + "' and pk_dmltnprcprg = '" + pk_dmltnprcprg + "'");
			if (result == null || result.length ==0 || result[0].getAttributeValue("nprice") == null){
				getHousePrice();
				MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001055")//@res "�Ҳ�����Ӧ�Ĳ�Ǩ�۸񷽰���"
);
				return;
			}
			getBillCardPanel().setHeadItem("nypricebeforedis", result[0].getAttributeValue("nprice"));
			getBillCardPanel().setHeadItem("nytotalbeforedis", result[0].getAttributeValue("nmny"));
			addAppendagePrice();

		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * modifier��������
	 * �������ڣ�2010-5-9 ����11:34:59
	 * ����������
	 * @param dataCtrl
	 * @param fpmtype
	 * @throws Exception
	 **/
	@SuppressWarnings("deprecation")
	protected void setMortgageValues(boolean dataCtrl,String fpmtype) throws Exception {

		//�ڽ���ˢ��ʱ��Ҫ����ҳǩ�ı༭��
		if(fpmtype == null || fpmtype.length()==0)
		  fpmtype = getBillCardPanel().getHeadItem("fpmtype").getValue();

	    // ɾ��������
		if(dataCtrl)
			deleteMortgagelines("ps_so_sign_mortgage");
//		deletelines("ps_so_subsc_mortgage");
		if (fpmtype != null && "0".equals(fpmtype)){
			if(!isListPanelSelected()){
				getBillCardPanel().getBodyTabbedPane().setEnabledAt(3, true);
				getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, true);	}
			else{
				getBillListPanel().getBodyTabbedPane().setEnabledAt(3, true);
				getBillListPanel().getBodyTabbedPane().setEnabledAt(7, true);
			}
			if(dataCtrl)
				setMortgage(0,false);
		}else {
			//����������࣬���ۿ�ҳǩ�ǻ���
			if(fpmtype != null && "2".equals(fpmtype)){
				if(!isListPanelSelected())
					getBillCardPanel().getBodyTabbedPane().setEnabledAt(3, false);
				else
					getBillListPanel().getBodyTabbedPane().setEnabledAt(3, false);
				}
			else{
				if(!isListPanelSelected())
					getBillCardPanel().getBodyTabbedPane().setEnabledAt(3, true);
				else
					getBillListPanel().getBodyTabbedPane().setEnabledAt(3, true);
				}
			//end by chixy
			if(!isListPanelSelected())
				getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, false);
			else
				getBillListPanel().getBodyTabbedPane().setEnabledAt(7, false);
			//getBillCardPanel().getBodyTabbedPane().setSelectedIndex(0);
			if(dataCtrl)
				setMortgage(1,false);
		}
	}
//	private void setMortgageValues() throws Exception {
//		String fpmtype = getBillCardPanel().getHeadItem("fpmtype").getValue();
//		deleteMortgagelines("ps_so_sign_mortgage");
//
//		if (fpmtype != null && "0".equals(fpmtype)){
//			getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, true);
//			setMortgage(0,false);
//		}else {
//			getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, false);
//			getBillCardPanel().getBodyTabbedPane().setSelectedIndex(0);
//			setMortgage(1,false);
//		}
//	}

	private void deleteMortgagelines(String tablecode) {
		// ���±���VOǰ���ɱ����б�ǳ�ɾ��
		Integer iRow = getBillCardPanel().getBillModel(tablecode).getRowCount();
		int[] dellinelist = new int[iRow];
		for (int i = 0; i < iRow; i++) {
			dellinelist[i] = i;
		}
		getBillCardPanel().getBodyPanel(tablecode).delLine(dellinelist);
	}

	/**
	 * ���ݱ�ͷ��װ�޵������ɱ���
	 */
	public void generateFitment() throws Exception {
		if (getBillCardPanel().getHeadItem("pk_fmcrit").getValueObject() == null) {
			getBillCardPanel().setHeadItem("fmname", null);
			getBillCardPanel().setHeadItem("nyfmprice", null);
			getBillCardPanel().setHeadItem("nfmprice", null);
			getBillCardPanel().setHeadItem("nftundismny", null);
			getBillCardPanel().setHeadItem("nftdismny", null);
			Integer iRow = getBillCardPanel().getBillModel("ps_so_sign_discount").getRowCount();
			for (int i = 0; i < iRow; i++) {
				getBillCardPanel().setBodyValueAt(true, i, "reserve15");
			}
			// ��ձ���
			deletelines();
			getBillCardPanel().getBillModel("ps_so_sign_fitment").execLoadFormula();
			return;
		}
		String pk_fitmentdoc = getBillCardPanel().getHeadItem("pk_fmcrit").getValueObject().toString();
		BdFitmemtdocBVO[] bdFitmemtdocBVOs = (BdFitmemtdocBVO[]) PSUIProxy.getIUifService().queryByCondition(BdFitmemtdocBVO.class,
				" pk_fitmemtdoc='" + pk_fitmentdoc + "' and isnull(dr,0)=0 ");
		if (bdFitmemtdocBVOs == null || bdFitmemtdocBVOs.length == 0)
			return;
		SoSubscFitmentVO[] fitmentVOs = new SoSubscFitmentVO[bdFitmemtdocBVOs.length];
		for (int i = 0; i < fitmentVOs.length; i++) {
			fitmentVOs[i] = new SoSubscFitmentVO();
			fitmentVOs[i].setPk_fitmentitem(bdFitmemtdocBVOs[i].getPk_fitmentitem());
		}
		deletelines();
		// ���±���VO���±����б�ǳ�����
		Integer iRowNew = fitmentVOs.length;
		for (int i = 0; i < iRowNew; i++) {
			getBillCardPanel().getBillModel("ps_so_sign_fitment").addLine();
			Integer iCurrow = getBillCardPanel().getBillModel("ps_so_sign_fitment").getRowCount() - 1;
			getBillCardPanel().getBillModel("ps_so_sign_fitment").setRowState(iCurrow, BillModel.ADD);
			getBillCardPanel().getBillModel("ps_so_sign_fitment").setValueAt(fitmentVOs[i].getPk_fitmentitem(), iCurrow, "pk_fitmentitem");
		}
		getBillCardPanel().getBillModel("ps_so_sign_fitment").execLoadFormula();
	}

	private void deletelines() {
		// ���±���VOǰ���ɱ����б�ǳ�ɾ��
		Integer iRow = getBillCardPanel().getBillModel("ps_so_sign_fitment").getRowCount();
		int[] dellinelist = new int[iRow];
		for (int i = 0; i < iRow; i++) {
			dellinelist[i] = i;
		}
		getBillCardPanel().getBillModel("ps_so_sign_fitment").delLine(dellinelist);
	}

	/**
	 * @�����ߣ�����
	 * @����˵�����޸�Ӧ��
	 * @����ʱ�䣺2008-1-4 ����10:38:59
	 * @param e
	 */
	private void modifyShouldMny(BillEditEvent e) {
		int count = e.getRow();
		String bisfund = getBillCardPanel().getBodyValueAt(count, "bisfund") == null ? null : getBillCardPanel().getBodyValueAt(count, "bisfund").toString();
		if (bisfund == null || bisfund.length() == 0) {
			return;
		}
		getBillCardPanel().getBillModel().setValueAt(UFBoolean.FALSE, e.getRow(), "bisfinish");
		getBillCardPanel().getBillModel().setValueAt(null, e.getRow(), "dfinishdate");
		Object dsigndate = getBillCardPanel().getHeadItem("dsigndate").getValueObject();
		UFDate dfinishdate = null; // �����������
		if (dsigndate == null || dsigndate.toString().trim().length() <= 0)
			dfinishdate = BillDateGetter.getMakeDate();
		else
			dfinishdate = new UFDate(dsigndate.toString());
		if (e.getValue() != null) {
			//note by chixy 2010-05-29
			//1.���ʵ�մ����㵫�������Ӧ��С��ʵ�գ���Ӧ����������Ϊԭ��ֵ������������Ч
			//2.���ʵ��Ϊ���ҵ������Ӧ��Ϊ�㣬�ý�������Ϊ���̬
			//3.Ӧ�մ��ڵ���ʵ�գ����¼����������̵�Ӧ�ս��
			//written off by chixy 2010-05-29 �ͻ����󣬿����޸�����ǩԼ������̬�����״̬���̵�Ӧ�ս�����С��ʵ�գ�Ӧ�յ��������·���ʵ��
//			if (getBillCardPanel().getBillModel().getValueAt(e.getRow(), "nyfactmny") != null) {
//				if (new UFDouble(e.getValue().toString()).compareTo(new UFDouble(getBillCardPanel().getBillModel().getValueAt(e.getRow(), "nyfactmny")
//						.toString())) < 0) {
//					getBillCardPanel().getBillModel().setValueAt(e.getOldValue(), e.getRow(), "nyshouldmny");
//					getBillCardPanel().getBillModel().execLoadFormula();
//					return;
//				}
//			}
			//end by chixy

			if (new UFDouble(e.getValue().toString()).doubleValue() == 0) {
				getBillCardPanel().getBillModel().setValueAt(UFBoolean.TRUE, e.getRow(), "bisfinish");
				getBillCardPanel().getBillModel().setValueAt(dfinishdate, e.getRow(), "dfinishdate");
			}
			try {
				SoCourseBVO[] coursebVOs = (SoCourseBVO[]) getBillCardPanel().getBillModel(getCoursePageSign()).getBodyValueVOs(SoCourseBVO.class.getName());
				try {
					if (e.getOldValue() == null) {
						calMny(new UFDouble(0), new UFDouble(e.getValue().toString()), e.getRow());
					} else
						calMny(new UFDouble(e.getOldValue().toString()), new UFDouble(e.getValue().toString()), e.getRow());
				} catch (BusinessException be) {
					getBillCardPanel().getBillModel(getCoursePageSign()).setBodyDataVO(coursebVOs);
					getBillCardPanel().getBillModel(getCoursePageSign()).setValueAt(e.getOldValue(), e.getRow(), e.getKey());
				}
			} catch (Exception ex) {
				MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000778")//@res "����Ӧ�ճ����쳣�� ��ˢ�½��棬����ҵ��"
);
				nc.bs.logging.Logger.error(ex.getMessage(), ex);
				return;
			}
		} else {
			getBillCardPanel().getBillModel().setValueAt(UFBoolean.TRUE, e.getRow(), "bisfinish");
			getBillCardPanel().getBillModel().setValueAt(dfinishdate, e.getRow(), "dfinishdate");
		}
		getBillCardPanel().getBillModel(getCoursePageSign()).execLoadFormula();
		setCourseCacheVO((SoCourseBVO[]) getBillCardPanel().getBillModel(getCoursePageSign()).getBodyValueVOs(SoCourseBVO.class.getName()));
	}

	/**
	 * @�����ߣ�tm
	 * @����˵����
	 * @����ʱ�䣺2007-11-28 ����02:28:51
	 * @�޸��ߣ�tm
	 * @�޸�ʱ�䣺2007-11-28 ����02:28:51
	 * note by chixy 2010-05-29 ��
	 * 		1.��editRowӦ�ս��༭�󣬽�������·��䵽δ��ɵĽ��̻�����ɵ�Ӧ��Ϊ��Ľ�����
	 * 		2.�����Ƿ���ʵ�ս��ֱ𽫲����ɵ������ϣ�������̲����Խ���������ϣ����䵽��һ����
	 * 		3.���ݷ�����Ϻ��Ӧ�պ�ʵ��������½��̵��Ƿ���ɼ���������ֶ�
	 * @param nbMny
	 *        �޸�ǰ�ļ۸�
	 * @param nafMny
	 *        �޸ĺ�ļ۸�
	 * @param editRow
	 *        ��ǰ�༭��
	 */
	private void calMny(UFDouble nbMny, UFDouble nafMny, int editRow) throws Exception {
		double subMny = (nafMny.sub(nbMny)).doubleValue(); // �ۺ��ܼ������Ӧ�յĲ��
		Object dsigndate = getBillCardPanel().getHeadItem("dsigndate").getValueObject();
		UFDate dfinishdate = null; // �����������
		if (dsigndate == null || dsigndate.toString().trim().length() <= 0)
			dfinishdate = BillDateGetter.getMakeDate();
		else
			dfinishdate = new UFDate(dsigndate.toString());
		SoCourseBVO[] courseBVO = (SoCourseBVO[]) getBillCardPanel().getBillModel().getBodyValueVOs(SoCourseBVO.class.getName());
		// ����޸ĺ�ļ۸�����޸�ǰ��
		for (int i = 0; i < courseBVO.length; i++) {
			// ���޸ĵ�ǰ�ı༭��
			if (courseBVO[i].getIpmnum().intValue() - 1 == editRow)
				continue;
			if (courseBVO[i].getBisfund() != null && courseBVO[i].getBisfund().booleanValue()) {
				//note by chixy �������δ��ɻ��߽�������˵���Ӧ��Ϊ�������Ӧ�ս������·���
				if ((courseBVO[i].getBisfinish() == null || !courseBVO[i].getBisfinish().booleanValue())
						|| (courseBVO[i].getBisfinish() != null && courseBVO[i].getBisfinish().booleanValue() && (courseBVO[i].getNyshouldmny() == null || (courseBVO[i]
								.getNyshouldmny() != null && courseBVO[i].getNyshouldmny().doubleValue() == 0)))) {
					BdFundsetVO fundsetVO = (BdFundsetVO) getBusiDelegator().queryByPrimaryKey(BdFundsetVO.class, courseBVO[i].getPk_fundset_fund());
					if (fundsetVO.getVfscode().equalsIgnoreCase(IPsDataMapping.FUND_HOUSE_LOAN)) {
						if (MessageDialog.showYesNoDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000779")//@res "�Ƿ���������ҿ"
) != MessageDialog.ID_YES)
							throw new BusinessException();
					}
					if (courseBVO[i].getNyshouldmny() == null)
						courseBVO[i].setNyshouldmny(new UFDouble(0));
					if (courseBVO[i].getNyshouldmny() != null) {
						getBillCardPanel().getBillModel().setRowState(i, BillModel.MODIFICATION);
						if (courseBVO[i].getNyfactmny() != null) {
							// ���Ӧ�պ�ʵ�ն���ֵ�������Ӧ����ʵ�ղ�
							// ��subMny�Ƚϣ�����0��ֱ�Ӱ�Ӧ�ռ�ȥsubMny�������������´�ѭ��
							UFDouble shouldMny = (courseBVO[i].getNyshouldmny().sub(courseBVO[i].getNyfactmny())).sub(subMny);
							if (shouldMny.doubleValue() >= 0) {
								getBillCardPanel().getBillModel().setValueAt(courseBVO[i].getNyshouldmny().sub(subMny),
										courseBVO[i].getIpmnum().intValue() - 1, "nyshouldmny");
								break;
							} else {
								if (courseBVO[i].getNyshouldmny().doubleValue() == 0)
									continue;
								getBillCardPanel().getBillModel().setValueAt(courseBVO[i].getNyfactmny(), courseBVO[i].getIpmnum().intValue() - 1,
										"nyshouldmny");
								subMny = subMny - courseBVO[i].getNyshouldmny().sub(courseBVO[i].getNyfactmny()).doubleValue();
								continue;
							}
						} else {

							// ��ʵ�յ�ʱ�򣬽���Ӧ���Ƿ���ڲ�ֵsubMny
							UFDouble shouldMny = courseBVO[i].getNyshouldmny().sub(subMny);
							if (shouldMny.doubleValue() >= 0) {

								getBillCardPanel().getBillModel().setValueAt(courseBVO[i].getNyshouldmny().sub(subMny),
										courseBVO[i].getIpmnum().intValue() - 1, "nyshouldmny");
								break;
							} else {
								if (courseBVO[i].getNyshouldmny().doubleValue() == 0)
									continue;
								getBillCardPanel().getBillModel().setValueAt(0, courseBVO[i].getIpmnum().intValue() - 1, "nyshouldmny");
								subMny = subMny - courseBVO[i].getNyshouldmny().doubleValue();
								continue;
							}
						}

					}
				}
			}
		}
		SoCourseBVO[] couBVO = (SoCourseBVO[]) getBillCardPanel().getBillModel().getBodyValueVOs(SoCourseBVO.class.getName());
		// ����޸ĺ�ļ۸�����޸�ǰ��
		for (int i = 0; i < couBVO.length; i++) {
			if (couBVO[i].getBisfund() != null && couBVO[i].getBisfund().booleanValue()) {
				//node by chixy �������δ�����ʵ�ս�������
				if (couBVO[i].getBisfinish() != null && couBVO[i].getBisfinish().booleanValue() && couBVO[i].getNyfactmny() != null
						&& couBVO[i].getNyfactmny().doubleValue() > 0)
					continue;
				if (couBVO[i].getNyshouldmny() != null) {
					if (couBVO[i].getNyfactmny() != null && couBVO[i].getNyfactmny().equals(couBVO[i].getNyshouldmny())) {
						getBillCardPanel().getBillModel().setValueAt(UFBoolean.TRUE, couBVO[i].getIpmnum().intValue() - 1, "bisfinish");
						getBillCardPanel().getBillModel().setValueAt(dfinishdate, couBVO[i].getIpmnum().intValue() - 1, "dfinishdate");
					} else if (couBVO[i].getNyshouldmny().doubleValue() == 0) {
						getBillCardPanel().getBillModel().setValueAt(UFBoolean.TRUE, couBVO[i].getIpmnum().intValue() - 1, "bisfinish");
						getBillCardPanel().getBillModel().setValueAt(dfinishdate, couBVO[i].getIpmnum().intValue() - 1, "dfinishdate");
					} else {
						getBillCardPanel().getBillModel().setValueAt(UFBoolean.FALSE, couBVO[i].getIpmnum().intValue() - 1, "bisfinish");
						getBillCardPanel().getBillModel().setValueAt(null, couBVO[i].getIpmnum().intValue() - 1, "dfinishdate");
					}
				} else {
					if ((couBVO[i].getNyfactmny() != null && couBVO[i].getNyfactmny().doubleValue() == 0) || couBVO[i].getNyfactmny() == null) {
						getBillCardPanel().getBillModel().setValueAt(UFBoolean.TRUE, couBVO[i].getIpmnum().intValue() - 1, "bisfinish");
						getBillCardPanel().getBillModel().setValueAt(dfinishdate, couBVO[i].getIpmnum().intValue() - 1, "dfinishdate");
					}
				}
			}
		}
	}

	public String getBillNo() throws java.lang.Exception {

		/**
		if (isFromSellCtrl())
			return null;
		 */
//		return super.getBillNo();
		//��̨�Զ����� 20111124
		return null;
		//return HYPubBO_Client.getBillNo(getUIControl().getBillType(), _getCorp().getPrimaryKey(), null, null);

	}

	/**
	 * @see nc.ui.trade.manage.BillManageUI#setBillNo()
	 * ���ߣ�������
	 * �������ڣ�2010-5-24 ����05:00:57
	 * �������������Ǵ˷�������ֹ���ݺ�����
	 * @throws Exception
	 */
	@Override
	protected void setBillNo() throws Exception {
		//super.setBillNo();
	}

	@Override
	protected AbstractManageController createController() {
		return new ClientController();
	}

	@Override
	public void setBodySpecialData(CircularlyAccessibleValueObject[] vos) throws Exception {

	}

	@Override
	protected void setHeadSpecialData(CircularlyAccessibleValueObject vo, int intRow) throws Exception {

	}

	@Override
	protected void setTotalHeadSpecialData(CircularlyAccessibleValueObject[] vos) throws Exception {

	}

	@Override
	protected void initSelfData() {
		// �ͻ�����
		getBillCardWrapper().initBodyComboBox("ps_so_sign_customer", "fccategory", IDownList.CUSTOMER_KIND, true);
		getBillListWrapper().initBodyComboBox("ps_so_sign_customer", "fccategory", IDownList.CUSTOMER_KIND, true);
		// �ͻ�����
		getBillListWrapper().initBodyComboBox("ps_so_sign_customer", "fcustype", IDownList.CUSTOMER_IDEA_TYPE, true);
		getBillCardWrapper().initBodyComboBox("ps_so_sign_customer", "fcustype", IDownList.CUSTOMER_IDEA_TYPE, true);
		// ���㷽ʽ
		getBillCardWrapper().initBodyComboBox("ps_so_sign_discount", "fdbaccountmode", IDownList.DISCOUNT_COUNT_TYPE, true);
		getBillListWrapper().initBodyComboBox("ps_so_sign_discount", "fdbaccountmode", IDownList.DISCOUNT_COUNT_TYPE, true);
		// �ۿ۷�ʽ
		getBillCardWrapper().initBodyComboBox("ps_so_sign_discount", "fdiscountmode", IDownList.DISCOUNT_TYPE, true);
		getBillListWrapper().initBodyComboBox("ps_so_sign_discount", "fdiscountmode", IDownList.DISCOUNT_TYPE, true);
		// �ۿ����ȼ�
		getBillCardWrapper().initBodyComboBox("ps_so_sign_discount", "fpriority", IDownList.REBATE_PRI, true);
		getBillListWrapper().initBodyComboBox("ps_so_sign_discount", "fpriority", IDownList.REBATE_PRI, true);
		// ��������
		/*getBillCardWrapper().initBodyComboBox("ps_so_course_b", "fpmcourse", IDownList.PAYMODE_SELL_COURSE, true);
		getBillListWrapper().initBodyComboBox("ps_so_course_b", "fpmcourse", IDownList.PAYMODE_SELL_COURSE, true);*/
		// ǰ�ý���
		/*getBillCardWrapper().initBodyComboBox("ps_so_course_b", "fbfcourse", IDownList.PAYMODE_BEFORE_COURSE, true);
		getBillListWrapper().initBodyComboBox("ps_so_course_b", "fbfcourse", IDownList.PAYMODE_BEFORE_COURSE, true);*/
		// ���ܵ�
		getBillCardWrapper().initBodyComboBox("ps_so_course_b", "ffunction", IDownList.SELL_COURSE_FUNCTION, true);
		getBillListWrapper().initBodyComboBox("ps_so_course_b", "ffunction", IDownList.SELL_COURSE_FUNCTION, true);
		// ��ɺ�״̬
		getBillCardWrapper().initBodyComboBox("ps_so_course_b", "fafstate", IDownList.PAYMODE_FINISH_STATUS, true);
		getBillListWrapper().initBodyComboBox("ps_so_course_b", "fafstate", IDownList.PAYMODE_FINISH_STATUS, true);
		// ���㷽ʽ
		getBillCardWrapper().initBodyComboBox("ps_so_course_b", "faccount", IDownList.PAYMODE_COUNT_TYPE, true);
		getBillListWrapper().initBodyComboBox("ps_so_course_b", "faccount", IDownList.PAYMODE_COUNT_TYPE, true);
		// ���뷽ʽ
		//add by magw 2010-12-2 ������Ҫ��������Ϊ��λ
		String[] fchoice=IDownList.PAYMODE_ROUND;
		String[] fchoice_wan=IDownList.PAYMODE_ROUND_WAN;
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i< fchoice.length;i++)
			list.add(fchoice[i]);
		list.add(fchoice_wan[0]);
		getBillCardWrapper().initBodyComboBox("ps_so_course_b", "fchoice", list.toArray(new String[0]), true);
		getBillListWrapper().initBodyComboBox("ps_so_course_b", "fchoice", list.toArray(new String[0]), true);

		// ���۷�ʽ
		String[] fsellmethod = IDownList.PRICE_SCHEME_STYLE;
		getBillCardWrapper().initBodyComboBox("ps_so_appendage", "fsellmethod", fsellmethod, true);
		getBillListWrapper().initBodyComboBox("ps_so_appendage", "fsellmethod", fsellmethod, true);

		// guhm
		// 2009-1-20
		// �ۿ۶���
		getBillCardWrapper().initBodyComboBox("ps_so_sign_discount", "discountobject", IDownList.DISOUNT_OBJECT, true);
		getBillListWrapper().initBodyComboBox("ps_so_sign_discount", "discountobject", IDownList.DISOUNT_OBJECT, true);
	}

	/**
	 * ���ñ��������Ϣ�� �������ڣ�(2004-3-26 15:04:09)
	 */
	private void afterCurrencyEdit(String pk_curr) {
		try {
			getBillCardPanel().setHeadItem("ncurrrate", getCy().getRate(pk_curr, getClientEnvironment().getDate().toString()));
		} catch (Exception ex) {
			nc.bs.logging.Logger.error(ex.getMessage(), ex);
		}
	}

	public CurrencyBusinessDelegator getCy() {
		if (cy == null)
			cy = new CurrencyBusinessDelegator(getClientEnvironment().getCorporation().getPk_corp());
		return cy;
	}

	@Override
	public void setDefaultData() throws Exception {
		getBillCardPanel().setHeadItem("pk_corp", getCorpPrimaryKey());

		getBillCardPanel().setHeadItem("dengdate", BillDateGetter.getBillDate());
		// �Ƶ�����
		getBillCardPanel().setTailItem("dmakedate", BillDateGetter.getMakeDate());
		// �Ƶ���
		getBillCardPanel().setTailItem("voperatorid", _getOperator());

		/**
		// �Զ����ɱ���  �ſ�bychixy��setbillNo��������д��billnoΪ�պ�̨У�鱨����������ʱ����������ݺ�
		getBillCardPanel().setHeadItem("vbillno", getBillNo());
		// ��ͬ��
		getBillCardPanel().setHeadItem("vbargainnum", getBillCardPanel().getHeadItem("vbillno").getValueObject());
		*/

		getBillCardPanel().setHeadItem("pk_billtype", getUIControl().getBillType());

		getBillCardPanel().setHeadItem("dsigndate", BillDateGetter.getBillDate());

		//�������
		getBillCardPanel().setHeadItem("despchgdate", BillDateGetter.getBillDate());

		getBillCardWrapper().getBillCardPanel().setHeadItem("vbillstatus", IBillStatus.FREE);

		SellPowerCtrl power = new SellPowerCtrl();
		String department = power.getDepartment();
		// ���ò���
		getBillCardPanel().setHeadItem("pk_deptdoc", department);
	}

	public ManageEventHandler createEventHandler() {
		return new ClientEventHandler(this, getUIControl());

	}

	private void editCourse(BillEditEvent e) {
		int count = e.getRow();
		String bisfund = getBillCardPanel().getBodyValueAt(count, "bisfund") == null ? null : getBillCardPanel().getBodyValueAt(count, "bisfund").toString();
		if (bisfund != null && !bisfund.equals("")) {
			if (new UFBoolean(bisfund).booleanValue()) {
				getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(count, "fundset", true);
				getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(count, "fundelse", true);
				getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(count, "nyshouldmny", true);
			} else {
				getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(count, "fundset", false);
				getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(count, "fundelse", false);
				getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(count, "nyshouldmny", false);
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "fundset");
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "fundelse");
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "pk_fundset_fund");
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "pk_fundset_else");
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "nshouldmny");
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "nyfactmny");
				getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, count, "nfactmny");

			}
		}
	}

	private void editCourse_b() {
		int rowCount = getBillCardPanel().getBillModel("ps_so_course_b").getRowCount();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				String bisfund = getBillCardPanel().getBillModel("ps_so_course_b").getValueAt(i, "bisfund") == null ? null : getBillCardPanel().getBillModel(
						"ps_so_course_b").getValueAt(i, "bisfund").toString();
				if (bisfund != null && bisfund.trim().length() > 0 && (new UFBoolean(bisfund).booleanValue() == true)) {
					getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(i, "fundset", true);
					getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(i, "fundelse", true);
					getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(i, "nyshouldmny", true);

				} else {
					getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(i, "fundset", false);
					getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(i, "fundelse", false);
					getBillCardPanel().getBillModel("ps_so_course_b").setCellEditable(i, "nyshouldmny", false);
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "fundset");
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "fundelse");
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "pk_fundset_fund");
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "pk_fundset_else");
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "nshouldmny");
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "nyfactmny");
					getBillCardPanel().getBillModel("ps_so_course_b").setValueAt(null, i, "nfactmny");

				}
			}
		}
	}

	@Override
	protected BusinessDelegator createBusinessDelegator() {
		return new LocalBusinessDelegator();
	}

	@SuppressWarnings("deprecation")
	public boolean beforeEdit(BillItemEvent e) {
		if (getBillOperate() != IBillOperate.OP_EDIT && getBillOperate() != IBillOperate.OP_REFADD && getBillOperate() != IBillOperate.OP_ADD)
			return true;
		if (getBillCardPanel().getHeadItem("pk_house").getValue() == null || getBillCardPanel().getHeadItem("pk_house").getValue().trim().length() == 0) {
			getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
			getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(false);
			getBillCardPanel().getHeadItem("pk_paymode").setValue(null);
			getBillCardPanel().getHeadItem("pk_fmcrit").setValue(null);
		} else {
			// ���������ʱ��������ҳǩ���������ܱ༭
			if (!getBisappendagechg()) {
				if (getBillCardPanel().getHeadItem("vbillstatus").getValueObject() != null
						&& Integer.parseInt(getBillCardPanel().getHeadItem("vbillstatus").getValueObject().toString()) == 1) {
					getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
				} else {
					Object pk_sign = getBillCardPanel().getHeadItem("pk_sign").getValueObject();
					if (pk_sign != null && pk_sign.toString().length() > 0) {
						// ����ǩԼ����տ
						CbGatheringVO[] cbVO = null;
						try {
							cbVO = (CbGatheringVO[]) new PSBusinessDelegator().queryByCondition(CbGatheringVO.class, "pk_sourcebill='" + pk_sign.toString() + "'",
									null, null, new String[] { "pk_sourcebill" });
						} catch (BusinessException ee) {
						}
						if(cbVO!=null && cbVO.length>0){
							getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
						}else{
							getBillCardPanel().getHeadItem("pk_paymode").setEnabled(true);
						}
					}else{
						getBillCardPanel().getHeadItem("pk_paymode").setEnabled(true);
					}
				}

				// ������������ʽ���ɵ�ǩԼ�������޸ĸ��ʽ
				Object bisfromch = getBillCardPanel().getHeadItem("bisfromch").getValueObject();
				Object bisfrompay = getBillCardPanel().getHeadItem("bisfrompay").getValueObject();
				if ((bisfrompay != null && (new UFBoolean (bisfrompay.toString())).booleanValue())
						|| (bisfromch != null && (new UFBoolean(bisfromch.toString()).booleanValue()))) {
					getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
				}

				if (!isBiscontractchg()) {
					getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(true);
					if (getBillCardPanel().getHeadItem("vlastbill").getValue() != null && getBillCardPanel().getHeadItem("vlastbill").getValue().equals("946D")) {
	//					getBillCardPanel().getHeadItem("pk_paymode").setEnabled(false);
						getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(false);
					}
				}
			}
			if (getBillCardPanel().getHeadItem("vbillstatus").getValueObject() != null
					&& Integer.parseInt(getBillCardPanel().getHeadItem("vbillstatus").getValueObject().toString()) == 1) {
				getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(false);
			}
		}

		if (getBillCardPanel().getHeadItem("vlastbill").getValueObject() != null
				&& getBillCardPanel().getHeadItem("vlastbill").getValueObject().toString().equals("946D")) {
			// ���ε���Ϊ�Ϲ����Ƿ�����״̬���ܸı�
			getBillCardPanel().getHeadItem("bispresent").setEnabled(false);
		} else {
			if (!isBiscontractchg())
				getBillCardPanel().getHeadItem("bispresent").setEnabled(true);
		}

		if (e.getItem().getKey().trim().equals("pk_house")) {

			UIRefPane refPanel = (UIRefPane) getBillCardWrapper().getBillCardPanel().getHeadItem("pk_house").getComponent();
			/**
			 * ������
			 */
			BdHouseRefModel panelModel = (BdHouseRefModel) refPanel.getRefModel();
			panelModel.clearCacheData();
			/** �����Ĳ��յĲ�ѯ������д��wherePart()��,�ͱ����Ȱ�wherePart()������,������Ĳ�ѯ��䲻���� */
			HouseStatesMapping stateMap = HouseStatesMapping.createHouseStatesMapping();
			String[] sellStates = ISellStatus.M_CANSELL;
			String wherePart = " and ( crm_bd_house.pk_sellstate in('";
			for (String str : sellStates) {
				wherePart += stateMap.getTempSellStatesR().get(str) + "','";
			}
			wherePart = wherePart.substring(0, wherePart.length() - 3);
			wherePart += "'))";

			/**
			if (getPk_project() != null && getPk_building() != null) {
				wherePart = wherePart + " and crm_bd_house.pk_project_build ='" + getPk_project() + "' and crm_bd_house.pk_building='" + getPk_building() + "'";
			} else if (getPk_project() != null && getPk_building() == null) {
				wherePart = wherePart + " and crm_bd_house.pk_project_build ='" + getPk_project() + "'";
			} else if (getPk_project() == null && getPk_building() != null) {
				wherePart = wherePart + " and crm_bd_house.pk_building='" + getPk_building() + "'";
			}
			*/
			panelModel.setPk_project(getPk_project());
			panelModel.setPk_building(getPk_building());
			panelModel.addWherePart(wherePart);

			refPanel.setRefModel(panelModel);
		}
		if (e.getItem().getKey().trim().equals("pk_paymode")) {
			String pk_house = getBillCardPanel().getHeadItem("pk_house").getValue() == null ? null : getBillCardPanel().getHeadItem("pk_house").getValue()
					.toString();
			if (pk_house == null || pk_house.equals("") || pk_house.toString().trim().length() <= 0) {
				return false;

			}
			// BdHouseVO houseVO = getHouseVOByPrimarykey(pk_house);
			UIRefPane refPanel = (UIRefPane) getBillCardWrapper().getBillCardPanel().getHeadItem("pk_paymode").getComponent();
			// *** ������ **//*
			PaymodeRefModel panelModel = (PaymodeRefModel) refPanel.getRefModel();
			panelModel.clearCacheData();
			String wherePart = " and ps_bd_paymode.pk_project in(select crm_bd_house.pk_project from crm_bd_house"
					+ " where crm_bd_house.pk_project=ps_bd_paymode.pk_project and crm_bd_house.pk_house='" + pk_house + "' and isnull(crm_bd_house.dr,0)=0 )";

			// �����Ƿ������ֶι��˸��ʽ
			String bispresent = getBillCardPanel().getHeadItem("bispresent").getValueObject() == null ? null : getBillCardPanel().getHeadItem("bispresent")
					.getValueObject().toString();
			if ((new UFBoolean(bispresent)).booleanValue()) {
				// ������
				wherePart += " and ps_bd_paymode.fpmtype='2'";
			} else {
				wherePart += " and ps_bd_paymode.fpmtype<>'2'";
			}

			if (getBillCardPanel().getHeadItem("vlastbill").getValueObject() != null
					&& getBillCardPanel().getHeadItem("vlastbill").getValueObject().toString().equals("946D")) {
				//	���ε���Ϊ�Ϲ�����������Ϲ� ǩԼ����
				wherePart += " and pk_paymode in(select pk_paymode from ps_bd_paymode_b where  fpmcourse = "
						+ IPsDataMapping.SELL_COURSE_SUBSCRIBE
						+ " and isnull(dr,0)=0 ) "
						+ " and pk_paymode in(select pk_paymode from ps_bd_paymode_b where  fpmcourse = "
						+ IPsDataMapping.SELL_COURSE_SIGNING
						+ " and isnull(dr,0)=0 )  and isnull(ps_bd_paymode.dr,0)=0";
			} else {
				// �������ǩԼ���� ��û���Ϲ�����.
				wherePart += " and pk_paymode in(select pk_paymode from ps_bd_paymode_b where  fpmcourse = "
						+ IPsDataMapping.SELL_COURSE_SIGNING
						+ " and isnull(dr,0)=0 ) "
						+
						// " and pk_paymode not in(select pk_paymode from
						// ps_bd_paymode_b where fpmcourse =
						// "+IPsDataMapping.SELL_COURSE_SUBSCRIBE+" and
						// isnull(dr,0)=0) and isnull(ps_bd_paymode.dr,0)=0 ";
						" and not exists  (select 'a' from  ps_bd_paymode_b  where ps_bd_paymode.pk_paymode=ps_bd_paymode_b.pk_paymode and  fpmcourse = "
						+ IPsDataMapping.SELL_COURSE_SUBSCRIBE + " and isnull(dr, 0) = 0) and isnull(ps_bd_paymode.dr,0)=0";
			}

			/*
			 * //ҵ̬���� wherePart += " and pk_situation='" + houseVO.getPk_situation() + "' ";
			 */
			panelModel.addWherePart(wherePart);
			refPanel.setRefModel(panelModel);
		}
		if (e.getItem().getKey().trim().equals("pk_fmcrit")) {
			String pk_house = getBillCardPanel().getHeadItem("pk_house").getValue() == null ? null : getBillCardPanel().getHeadItem("pk_house").getValue()
					.toString();
			@SuppressWarnings("unused")
			String pk_currtype = getBillCardPanel().getHeadItem("pk_currtype").getValue();
			if (pk_house == null || pk_house.equals("") || pk_house.toString().trim().length() <= 0) {
				return false;

			}
			UIRefPane refPanel = (UIRefPane) getBillCardWrapper().getBillCardPanel().getHeadItem("pk_fmcrit").getComponent();
			/** * ������ * */
			BdFitmentRefModel panelModel = (BdFitmentRefModel) refPanel.getRefModel();
			panelModel.clearCacheData();
			String wherePart = " and pk_project in(select crm_bd_house.pk_project from crm_bd_house" + " where crm_bd_house.pk_house='" + pk_house
					+ "' and isnull(crm_bd_house.dr,0)=0 ) ";
			panelModel.addWherePart(wherePart);
			refPanel.setRefModel(panelModel);
		}
		if (e.getItem().getKey().trim().equals("bactmny")) {
			String pk_lastbill = getBillCardPanel().getHeadItem("pk_lastbill").getValue();
			if (pk_lastbill != null && pk_lastbill.trim().length() > 0) {
				getBillCardPanel().getHeadItem("bactmny").setEnabled(false);//setEdit(false);
			} else {
				getBillCardPanel().getHeadItem("bactmny").setEnabled(true);
			}
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		if (e.getKey().trim().equals("cusname")) {
			if (getBillCardPanel().getHeadItem("pk_lastbill").getValue() != null && getBillCardPanel().getHeadItem("pk_lastbill").getValue().length() > 0) {
				String vlb = getBillCardPanel().getHeadItem("vlastbill").getValue();
				if (vlb != null && !vlb.trim().equals(IPsDataMapping.SO_QUEUE)) {
					if (e.getPos() == 1 && e.getTableCode().equals("ps_so_sign_customer")) {
						//	�����Ϲ��Ŀͻ������޸ġ�ɾ��
						if (getBillCardPanel().getBodyValueAt(e.getRow(), "bfromsubsc") != null
								&& new UFBoolean(getBillCardPanel().getBodyValueAt(e.getRow(), "bfromsubsc").toString()).booleanValue()) {
							return false;
						} else if (getBillCardPanel().getHeadItem("vbillstatus").getValue() != null
								&& getBillCardPanel().getHeadItem("vbillstatus").getValue().trim().equals("1")) {
							return false;
						}
					}
//					getBillCardPanel().getBillModel(getFirstPageSign()).getItemByKey("cusname").setEdit(false);
//					return false;
				}
			}
			String pk_house = getHeadItemString("pk_house");
			if (pk_house == null || pk_house.trim().length() == 0) {
				MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000998")//@res "����ѡ�񷿲�!"
);
				return false;
			}
			String pk_project = getHeadItemString("pk_project");
			UIRefPane refPanel = (UIRefPane) getBillCardPanel().getBodyItem("cusname").getComponent();
			Ps_cmg_customerRefModel customerRefModel = (Ps_cmg_customerRefModel) refPanel.getRefModel();
			customerRefModel.clearCacheData();
			try {
				customerRefModel.setAddSql("sign");
				customerRefModel.setPk_house(pk_house);
				customerRefModel.setPk_project(pk_project);
				customerRefModel.addWherePart(" and ps_cmg_customer.pk_customer in("
						+ (new SellPowerCtrl()).getCustomerShowsqlSelect(getHeadItemString("pk_project")) + " ) ");
			} catch (Exception e1) {
				nc.bs.logging.Logger.error(e1.getMessage(), e1);
			}
			refPanel.setRefModel(customerRefModel);
		}
		// �ۿ� ��Ŀ����
		if (e.getKey().trim().equals("vdbcode")) {
			String pk_house = getBillCardPanel().getHeadItem("pk_house").getValue() == null ? null : getBillCardPanel().getHeadItem("pk_house").getValue()
					.toString();
			if (pk_house == null || pk_house.trim().length() == 0) {
				MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000998")//@res "����ѡ�񷿲�!"
);
				return false;
			}
			BdHouseVO houseVO = getHouseVOByPrimarykey(pk_house);
			UIRefPane refPanel = (UIRefPane) getBillCardPanel().getBodyItem("vdbcode").getComponent();
			PrmDiscountRefGridTreeModel customerRefModel = (PrmDiscountRefGridTreeModel) refPanel.getRefModel();
			customerRefModel.clearCacheData();

			//add by chixy �ۿ۲�����߷�����Ҫ������ѡ��Ŀ����
			customerRefModel.setPk_project(new String[]{houseVO.getPk_project()});
			//end by chixy

			if (getPk_cur() == null) {
				setPk_cur(getBillCardPanel().getHeadItem("pk_cur").getValue());
			} else if (getPk_cur() != null && getPk_cur().length() > 0)
				customerRefModel.addWherePart(" and pk_project = '" + houseVO.getPk_project() + "' and ps_prm_discount.pk_currtype ='" + getPk_cur() + "'");
			else
				customerRefModel.addWherePart(" and pk_project = '" + houseVO.getPk_project() + "'");

			refPanel.setRefModel(customerRefModel);
		}

		if (e.getPos() == 1 && e.getTableCode().equals("ps_so_sign_customer")) {
			//	�����Ϲ��Ŀͻ������޸ġ�ɾ��
			if (getBillCardPanel().getBodyValueAt(e.getRow(), "bfromsubsc") != null
					&& new UFBoolean(getBillCardPanel().getBodyValueAt(e.getRow(), "bfromsubsc").toString()).booleanValue()) {
				//��Ȩ���������޸�
				if(e.getKey().equals("nproperty")){
					return true;
				}else{
					return false;
				}
			} else if (getBillCardPanel().getHeadItem("vbillstatus").getValue() != null
					&& !getBillCardPanel().getHeadItem("vbillstatus").getValue().trim().equals("1")) {
				getBillCardPanel().getBillModel(getFirstPageSign()).getItemByKey("cusname").setEnabled(true);
			}
		}

		if (e.getPos() == 1 && e.getTableCode().equals("ps_so_course_b")) {

			//note by chixy ��ҳǩ�л��¼����Ѿ�������editCourse_b�����˷ǿ�����̷�Ӧ�ս���ֶβ��ܱ༭
			if (!e.getKey().equals("nyshouldmny") && getBillCardPanel().getBodyValueAt(e.getRow(), "nyfactmny") != null
					&& new UFDouble(getBillCardPanel().getBodyValueAt(e.getRow(), "nyfactmny").toString()).doubleValue() > 0) {
				return false;// ��ʵ�ղ������޸������ֶ�
			}

			//add by chixy 2010-06-27 װ�޿����Ӧ����ʱ���ɵ�
			SoCourseBVO cosVO = (SoCourseBVO)getBillCardPanel().getBillModel("ps_so_course_b").
									getBodyValueRowVO(e.getRow(), SoCourseBVO.class.getName());
			if(cosVO.getFpmcourse().intValue()== IPsDataMapping.SELL_COURSE_FTIMENT )
				return false;
			//end by chixy

			//�ͻ���������ǩԼ������ͨ��̬ʱ�����޸�Ӧ�ս������������ɵ�ǩԼ��������
			if (getBillCardPanel().getBodyValueAt(e.getRow(), "bisfund") == null
					|| getBillCardPanel().getBodyValueAt(e.getRow(), "bisfund").toString().trim().equals("false")) {
				//ǩԼ������δ��ɽ���Ӧ������ڿ����޸�
				if (cosVO.getFpmcourse() != null && cosVO.getFpmcourse().intValue() > IPsDataMapping.SELL_COURSE_SIGNING) {
					if(getBillCardPanel().getBodyValueAt(e.getRow(), "bisfinish") == null
							|| getBillCardPanel().getBodyValueAt(e.getRow(), "bisfinish").toString().trim().equals("false")){
						if(e.getKey().equals("dbefinishdate"))
							return true;
					}
				}
				return false;// �ǿ�����̲������޸�
			}
			if (getBillCardPanel().getBodyValueAt(e.getRow(), "bisfinish") != null
					&& getBillCardPanel().getBodyValueAt(e.getRow(), "bisfinish").toString().trim().equals("false")) {
					return true;
			} else {

				//��˾�����Ƿ���Ե�����ɽ���
				String flagStr = PSParamReader.getParamter(_getCorp().getPrimaryKey(), PSParamReader.BD_PS_OVERCOURSE_MODI);
				if("N".equals(flagStr))
					return false;
				if(isFromSpecialCourse() || isApproveBill())
					return false;
				else
					return true;
				//return false;// ����ɽ��̲������޸�
				//end by chixy
			}
		}
		if (e.getPos() == 1 && e.getTableCode().equals("ps_so_sign_discount")) {
			if (e.getKey().equals("nythisdiscountmny") && getBillCardPanel().getBodyValueAt(e.getRow(), "discountobject") != null
					&& getBillCardPanel().getBodyValueAt(e.getRow(), "discountobject").toString().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000775")//@res "װ�޿�"
)) {
				return false;
			}
			if (e.getKey().equals("nyfitmentdiscountmny") && getBillCardPanel().getBodyValueAt(e.getRow(), "discountobject") != null
					&& getBillCardPanel().getBodyValueAt(e.getRow(), "discountobject").toString().equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001056")//@res "�����λ��"
)) {
				return false;
			}
			if ((e.getKey().equals("nythisdiscountmny") || e.getKey().equals("nyfitmentdiscountmny"))
					&& (getBillCardPanel().getBodyValueAt(e.getRow(), "bisrefmodify") == null || getBillCardPanel().getBodyValueAt(e.getRow(), "bisrefmodify")
							.toString().equals("false"))) {
				return false;
			}
		}

		// ���������ʱ��������ҳǩ���������ܱ༭
		if (getBisappendagechg()) {
			if (e.getPos() == 0 || (e.getPos() == 1 && !e.getTableCode().trim().equals("ps_so_appendage"))) {
				return false;
			}
		}

		if (e.getPos() == 1 && e.getTableCode().equals("ps_so_appendage")) {

			BillModel bm = getBillCardPanel().getBillModel("ps_so_appendage");

			if (e.getKey().equals("vhname")) {

				/**
				 * �Լ��뷿�۵ĸ������������޸ģ�
				 * @modified by zhangws
				 */

				Object bisAddmny = bm.getValueAt(e.getRow(), "biscombined");
				if(bisAddmny!=null && (Boolean)bisAddmny){
					showHintMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-001057")//@res "��ʾ�����뷿�۵ĸ������������޸�!"
);
					return false;
				}

				String pk_house = getBillCardPanel().getHeadItem("pk_house").getValueObject() == null ? null : getBillCardPanel().getHeadItem("pk_house")
						.getValueObject().toString();
				if (pk_house == null || pk_house.trim().length() == 0) {
					MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000998")//@res "����ѡ�񷿲�!"
);
					return false;
				}
				String pkProject = getBillCardPanel().getHeadItem("pk_project").getValueObject() == null ? null : getBillCardPanel().getHeadItem("pk_project")
						.getValueObject().toString();
				if (pkProject == null || pkProject.trim().length() == 0) {
					MessageDialog.showHintDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000046")//@res "����ѡ����Ŀ!"
);
					return false;
				}

				UIRefPane refPanel = (UIRefPane) bm.getItemByKey("vhname").getComponent();
				/**
				 * ������
				 */
				BdHouseRefModel panelModel = (BdHouseRefModel) refPanel.getRefModel();
				panelModel.clearCacheData();
				String pk_project = null;
				//���ݱ�ͷ��������Ŀ���˱��帽��������Ŀ�µķ���
				if (pkProject != null&& !"".equals(pkProject)) {
					String whereSql = " isnull(dr,0)=0 and vincode = (select substring(vincode,1,2) from fdc_bd_project where pk_project ='" + pkProject + "')";
					BusinessDelegator delegater = new BusinessDelegator();
					try {
						BdProjectVO[]  bdProject = (BdProjectVO[])delegater.queryByCondition(BdProjectVO.class, whereSql);
						if(bdProject!=null && bdProject.length>0 && bdProject[0]!=null &&  bdProject[0].getPk_project()!=null ){
							pk_project = bdProject[0].getPk_project();
						}
					} catch (Exception e1) {
						Logger.error("��ѯ��������Ŀ����",e1);
					}
				}
				if(SafeObject.isNull(pk_project)){
					pk_project = pkProject;
				}
				panelModel.setPk_project(pk_project);
//				panelModel.setPk_building(getPk_building());//����������Ҫ¥������
				panelModel.addWherePart(getAppendageHouseRefSql(pk_house,pk_project));
				refPanel.setRefModel(panelModel);
			}

			String pk_house = bm.getValueAt(e.getRow(), "pk_house") == null ? null : bm.getValueAt(e.getRow(), "pk_house").toString();
			if (pk_house != null) {
				BdHouseVO houseVO = getHouseVOByPrimarykey(pk_house);

				if (e.getKey().equals("bissingle")) {
					HouseStatesMapping stateMap = HouseStatesMapping.createHouseStatesMapping();
					for (String str : ISellStatus.M_BESALE) {
						if (houseVO.getPk_sellstate().equals(stateMap.getTempSellStatesR().get(str))) {
							// ���������Ϲ���ǩԼ���Ƿ񵥶����۲��ܸı�
							return false;
						}
					}

					if (bm.getValueAt(e.getRow(), "biscombined") != null) {
						UFBoolean biscombined = new UFBoolean(bm.getValueAt(e.getRow(), "biscombined")
								.toString());
						if (biscombined.booleanValue()) {
							// ���뷿�����ܵ�������
							return false;
						}
					}
				}

				if (e.getKey().equals("biscombined")) {
					if (getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "bissingle") != null) {
						UFBoolean bissingle = new UFBoolean(getBillCardPanel().getBillModel("ps_so_appendage").getValueAt(e.getRow(), "bissingle").toString());
						if (bissingle.booleanValue()) {
							// �����������ܼ��뷿��
							return false;
						}
					}
				}
			} else {
				if (!e.getKey().equals("vhname")) {
					return false;
				}
			}

			if (getBisappendagechg()) {
				// ���������ʱ���ܼ��뷿��
				getBillCardPanel().getBillModel("ps_so_appendage").getItemByKey("biscombined").setEnabled(false);
			}

			if (e.getKey().equals("biscombined")) {
				String bispresent = getBillCardPanel().getHeadItem("bispresent").getValueObject() == null ? null : getBillCardPanel().getHeadItem("bispresent")
						.getValueObject().toString();
				if (bispresent != null && new UFBoolean(bispresent).booleanValue()) {
					// �������͸��������ܼ��뷿��
					return false;
				}
			}
		}

		return super.beforeEdit(e);
	}

	private String getAppendageHouseRefSql(String pk_house,String pk_project){
		/** �����Ĳ��յĲ�ѯ������д��wherePart()��,�ͱ����Ȱ�wherePart()������,������Ĳ�ѯ��䲻���� */
		HouseStatesMapping stateMap = HouseStatesMapping.createHouseStatesMapping();
		String[] sellStates = new String[ISellStatus.M_BESALE.length];
		for (int i = 0; i < ISellStatus.M_BESALE.length; i++) {
			if (ISellStatus.M_BESALE[i] == ISellStatus.H_BESALE)
				sellStates[i] = ISellStatus.H_CANSELL;
			else if (ISellStatus.M_BESALE[i] == ISellStatus.H_PRECONCERT)
				sellStates[i] = ISellStatus.H_QUEUE;
			else
				sellStates[i] = ISellStatus.M_BESALE[i];
		}
		String wherePart = " and ( crm_bd_house.pk_sellstate in('";
		for (String str : sellStates) {
			wherePart += stateMap.getTempSellStatesR().get(str) + "','";
		}
		wherePart = wherePart.substring(0, wherePart.length() - 3);
		wherePart += "'))";

		// ���и��������Ϲ���ǩԼ���ܱ�ѡ��
		wherePart += " and crm_bd_house.pk_house not in (select ps_so_subsc.pk_house from ps_so_subsc join ps_so_appendage on ps_so_appendage.pk_subsc=ps_so_subsc.pk_subsc where isnull(ps_so_subsc.dr,0)=0 and (ps_so_subsc.reserve1 is null or ps_so_subsc.reserve1='') and (ps_so_subsc.pk_nextbill is null or ps_so_subsc.pk_nextbill='') and isnull(ps_so_appendage.dr,0)=0)"
				+ " and crm_bd_house.pk_house not in (select ps_so_sign.pk_house from ps_so_sign join ps_so_appendage on ps_so_appendage.pk_sign=ps_so_sign.pk_sign where isnull(ps_so_sign.dr,0)=0 and (ps_so_sign.reserve1 is null or ps_so_sign.reserve1='') and isnull(ps_so_appendage.dr,0)=0)";

		if (SafeObject.isNotNull(pk_project)){
			SellPowerCtrl sellPowerCtrl = new SellPowerCtrl();
			try {
				wherePart += " and crm_bd_house.pk_project_build in (" + sellPowerCtrl.getSellPostAllPkproject(pk_project)+ ")";
			} catch (Exception e) {

			}
		}

		wherePart += " and crm_bd_house.pk_house<>'" + pk_house + "'";

		wherePart += " and not exists (select 1 from ps_so_appendage where ps_so_appendage.pk_house=crm_bd_house.pk_house and isnull(ps_so_appendage.dr,0)=0"
			+ " and (exists(select 1 from ps_so_sign where ps_so_sign.pk_sign=ps_so_appendage.pk_sign and isnull(ps_so_sign.dr,0)=0 and (ps_so_sign.reserve1 is null or ps_so_sign.reserve1=''))"
			+ " or exists(select 1 from ps_so_subsc where ps_so_subsc.pk_subsc=ps_so_appendage.pk_subsc and isnull(ps_so_subsc.dr,0)=0 and (ps_so_subsc.reserve1 is null or ps_so_subsc.reserve1='') and (ps_so_subsc.pk_nextbill is null or ps_so_subsc.pk_nextbill='')))"
			+ ")";

		return wherePart;
	}

	/**
	 *
	 * ���ߣ�������
	 * �������ڣ�2010-5-29 ����02:44:30
	 * �������������ݽ�����Ϣ�ж�ǩԼ���Ƿ��������������տ�����
	 * @return
	 **/
	public boolean isFromSpecialCourse(){

		boolean flag  = false;
		// ������������ʽ���ɵ�ǩԼ��
		Object bisfromch = getBillCardPanel().getHeadItem("bisfromch").getValueObject();
		Object bisfrompay = getBillCardPanel().getHeadItem("bisfrompay").getValueObject();
		if ((bisfrompay != null && (new UFBoolean (bisfrompay.toString())).booleanValue())
				|| (bisfromch != null && (new UFBoolean(bisfromch.toString()).booleanValue()))) {
			flag = true;
		}
		return flag;
	}

	/**
	 *
	 * ���ߣ�������
	 * �������ڣ�2010-5-29 ����03:11:18
	 * �����������жϵ�ǰ���������Ƿ�������ͨ��̬
	 * @return
	 **/
	public boolean isApproveBill(){

		boolean flag  = false;
		Object appObj = getBillCardPanel().getHeadItem("vbillstatus").getValueObject();
		if(appObj!=null && appObj.toString().equals(IBillStatus.CHECKPASS+""))
			flag = true;
		return flag;
	}

	public void appendagestatechange(boolean state) {
		getBillCardPanel().getBillModel("ps_so_appendage").getItemByKey("vhname").setEnabled(state);
		getBillCardPanel().getBillModel("ps_so_appendage").getItemByKey("biscombined").setEnabled(state);
		getBillCardPanel().getBillModel("ps_so_appendage").getItemByKey("vmark").setEnabled(state);
		getBillCardPanel().getBillModel("ps_so_appendage").getItemByKey("bissingle").setEnabled(state);
	}

	@SuppressWarnings( { "deprecation", "unchecked" })
	@Override
	public Object getUserObject() {
		ArrayList list = new ArrayList(); // Ҫ���ص�userList
		HYBillVO hyBillVO = null; // ��ǰ����VO
		SoSignVO soSignVO = null; // ��ǰ��������VO
		SoSignCustomerVO[] soSignCustVO = null; // ��ǰ���ݿͻ��ӱ�VO����
		String[] strCustomerPk = null; // ǩԼ�ͻ�PK����
		ArrayList al = new ArrayList();
		String pk_house = getBillCardPanel().getHeadItem("pk_house").getValue() == null ? null : getBillCardPanel().getHeadItem("pk_house").getValue()
				.toString();

		String pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();

		al.add(0, this.getOPEREATE());
		al.add(1, pk_house);
		al.add(2, pk_corp);
		al.add(3, getCourseCacheVO());
		al.add(4, isBtranstomem());
		al.add(5, getPk_customers());

		try {
			// added by liuhao @20120418
			// modified by liujian 
			if (!isListPanelSelected()) {
				hyBillVO = (HYBillVO) getBillCardWrapper().getBillVOFromUI();
			}else{
			    hyBillVO = (HYBillVO)this.getBufferData().getCurrentVO();
			}
			// modified by liujian
			// added by liuhao @20120418
			soSignVO = (SoSignVO) hyBillVO.getParentVO();
			if (hyBillVO.getTableVO("ps_so_sign_customer") != null) {
				CircularlyAccessibleValueObject[] tempVOs = hyBillVO.getTableVO("ps_so_sign_customer");
				soSignCustVO = new SoSignCustomerVO[tempVOs.length];
				for (int i = 0; i < tempVOs.length; i++) {
					soSignCustVO[i] = (SoSignCustomerVO) tempVOs[i];
				}
				strCustomerPk = new String[soSignCustVO.length];
				for (int i = 0; i < soSignCustVO.length; i++) {
					strCustomerPk[i] = soSignCustVO[i].getPk_customer();
				}
			}

			list.add(0, soSignVO.getDsigndate());
			list.add(1, soSignVO.getNtotalbeforedis());
			list.add(2, soSignVO.getNtotalmnysign());
			list.add(3, soSignVO.getNytotalbeforedis());
			list.add(4, soSignVO.getNytotalmnysign());
			list.add(5, soSignVO.getNyfmprice());
			list.add(6, strCustomerPk);
			list.add(7, soSignVO.getPk_billtype());
			list.add(8, soSignVO.getVbillno());
			list.add(9, soSignVO.getPk_lastbill());
			list.add(10, soSignVO.getVlastbill());
			list.add(11, al);

		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		return list;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void stateChanged(ChangeEvent e) {
		super.stateChanged(e);
		try {
			AggregatedValueObject billvo = null;
			if (isListPanelSelected())
				billvo = getBufferData().getCurrentVO();
			else
				billvo = getVOFromUI();
			if (billvo != null) {
				SoSignVO signvo = (SoSignVO) billvo.getParentVO();
				if (signvo != null  && signvo.getVbillstatus() != null && signvo.getVbillstatus() == IBillStatus.CHECKPASS && signvo.getBctaudit() != null && signvo.getBctaudit().booleanValue()) {
					//	ǩԼ����ͨ��������������ǩԼ���ϲ��䡢����������ť������
					getButtonManager().getButton(IPSButton.ChangeContractBtn).setEnabled(false);
					getButtonManager().getButton(IPSButton.ContractAuditBtn).setEnabled(false);
					updateButtons();
				}
			}
		} catch (Exception e1) {
			nc.bs.logging.Logger.error(e1.getMessage(), e1);
		}
		if (getBillOperate() == IBillOperate.OP_EDIT || getBillOperate() == IBillOperate.OP_ADD || getBillOperate() == IBillOperate.OP_REFADD) {
			onLineButtonCtrl();
			// ���ѡ���˹��̿�ַ��� ���в���������
			if (!isListPanelSelected() && getBillCardPanel().getBodyTabbedPane().getSelectedIndex() == 0
					&& getBillCardPanel().getHeadItem("bactmny").getValue() != null && getBillCardPanel().getHeadItem("bactmny").getValue().equals("true")) {
				getButtonManager().getButton(IBillButton.Line).setEnabled(false);
				setCurrentPanel(BillTemplateWrapper.CARDPANEL);
			} else if (!isListPanelSelected() && getBillCardPanel().getBodyTabbedPane().getSelectedIndex() == getFirstPageIndex()) {

				if (getBillCardPanel().getHeadItem("pk_lastbill").getValue() != null
						&& getBillCardPanel().getHeadItem("pk_lastbill").getValue().length() > 0) {

					String vlb = getBillCardPanel().getHeadItem("vlastbill").getValue();
					if (vlb != null && vlb.trim().equals(IPsDataMapping.SO_QUEUE)) {
						getButtonManager().getButton(IBillButton.Line).setEnabled(true);
					} else {
						getButtonManager().getButton(IBillButton.Line).setEnabled(false);
					}

					setCurrentPanel(BillTemplateWrapper.CARDPANEL);
					if (getBillCardPanel().getHeadItem("bactmny").getValue() == null
							|| getBillCardPanel().getHeadItem("bactmny").getValue().equals("false")) {
						getBillCardPanel().getBillModel(getFirstPageSign()).getItemByKey("cusname").setEnabled(true);
					}

				}
//				else {
//					getButtonManager().getButton(IBillButton.Line).setEnabled(true);
//					setCurrentPanel(BillTemplateWrapper.CARDPANEL);
//				}
			}

			if (!isListPanelSelected() && getBillCardPanel().getBodyTabbedPane().getSelectedIndex() == getCoursePageIndex()) {
				editCourse_b();
			}

			if (pk_lastbill!= null && !"".equals(pk_lastbill)) {
				if ((getBillCardPanel().getBodyTabbedPane().getSelectedIndex() == 7)) {
					getButtonManager().getButton(IBillButton.Line).setEnabled(false);
					setCurrentPanel(BillTemplateWrapper.CARDPANEL);
				}
			}

			// �Ϲ�תǩԼ��ǩԼδ����ͨ��ʱ�ſ��ͻ�ҳǩ���в��� 2010-10-28 ½��
			if(!isListPanelSelected() && getBillCardPanel().getBodyTabbedPane().getSelectedIndex() == getFirstPageIndex()
					&& getBillCardPanel().getHeadItem("vbillstatus").getValue() != null
					&& !getBillCardPanel().getHeadItem("vbillstatus").getValue().trim().equals("1")){
				String vlb=getBillCardPanel().getHeadItem("vlastbill").getValue();
				if(vlb !=null&&vlb.trim().equals(IPsDataMapping.SO_SUBSCRIBE)){
					getButtonManager().getButton(IBillButton.Line).setEnabled(true);
					setCurrentPanel(BillTemplateWrapper.CARDPANEL);
					return;
				}
			}
		}
		try {
			discountRate();

		} catch (Exception ex) {
			nc.bs.logging.Logger.error(ex.getMessage(), ex);
		}

	}

	@Override
	public void setHeadItem(CircularlyAccessibleValueObject cirVO) throws Exception {
		if (cirVO instanceof SoQueueVO) {
			SoQueueVO queueVO = (SoQueueVO) cirVO;
			// /getBillCardPanel().setHeadItem("nysmallsubscription",
			// queueVO.getNyqueuemny()); //ԭ��Ӧ���źŷ�->ԭ��Ӧ��С�����ܶ�
			// getBillCardPanel().setHeadItem("nyhaverecieved",
			// queueVO.getNyhaverecieved()); //ԭ�������źŷ�->ԭ������С����
			// getBillCardPanel().setHeadItem("nyhavereturn",
			// queueVO.getNyhavereturn()); //ԭ�������źŷ�->ԭ������С����
			getBillCardPanel().setHeadItem("pk_queue", queueVO.getPrimaryKey());
			getBillCardPanel().setHeadItem("pk_currtype", queueVO.getPk_currtype());
			// getCy().getCurrTypeVO(queueVO.getPk_currtype())
			// getBillCardPanel().setHeadItem("ncurrrate",
			// queueVO.getNcurrrate());
			getBillCardPanel().setHeadItem("ncurrrate", getCy().getRate(queueVO.getPk_currtype(), getClientEnvironment().getDate().toString()));
			getBillCardPanel().setHeadItem("pk_lastbill", queueVO.getPrimaryKey());
			getBillCardPanel().setHeadItem("vlastbill", IPsDataMapping.SO_QUEUE);
			getBillCardPanel().setHeadItem("vlastbillcode", queueVO.getVbillno());
			getBillCardPanel().setHeadItem("reserve5", queueVO.getTs());
			getBillCardPanel().setHeadItem("ts", queueVO.getTs());
			//modify by wangjiang at 20100513
			execPriceInfoFormulas();
//			getBillCardPanel().execHeadFormulas(
//					new String[] { "pk_price->getColValue(crm_bd_price, pk_price, pk_house, pk_house);",
//							"sellarea->getColValue(crm_bd_house, nsellarea, pk_house, pk_house);",
//							"nypricebeforedis->getColValue(crm_bd_price, nyprice, pk_price, pk_price);",
//							"nytotalbeforedis->getColValue(crm_bd_price, nymny, pk_price, pk_price);" });
			// ������
			updateShowDigitsByCard();
			generateDiscountByPrimaryKey(queueVO.getPk_discount());
		} else if (cirVO instanceof SoEngageVO) {
			SoEngageVO engageVO = (SoEngageVO) cirVO;
			getBillCardPanel().setHeadItem("nysmallsubscription", engageVO.getNysmallsubscription());
			getBillCardPanel().setHeadItem("pk_queue", engageVO.getPk_queue());
			getBillCardPanel().setHeadItem("pk_currtype", engageVO.getPk_currtype());
			// getBillCardPanel().setHeadItem("ncurrrate",
			// engageVO.getNcurrrate());
			getBillCardPanel().setHeadItem("ncurrrate", getCy().getRate(engageVO.getPk_currtype(), getClientEnvironment().getDate().toString()));
			generateDiscountByPrimaryKey(engageVO.getPk_discount());
			getBillCardPanel().setHeadItem("pk_lastbill", engageVO.getPrimaryKey());
			getBillCardPanel().setHeadItem("vlastbill", IPsDataMapping.SO_PRECONCERT);
			getBillCardPanel().setHeadItem("vlastbillcode", engageVO.getVbillno());
			getBillCardPanel().setHeadItem("reserve5", engageVO.getTs());
			getBillCardPanel().setHeadItem("ts", engageVO.getTs());
			//modify by wangjiang at 20100602
			execPriceInfoFormulas();
			// ������
			updateShowDigitsByCard();
		} else if (cirVO instanceof SoSubscVO) {
			SoSubscVO subscVO = (SoSubscVO) cirVO;
			getBillCardPanel().setHeadItem("pk_queue", subscVO.getPk_queue());
			getBillCardPanel().setHeadItem("pk_currtype", subscVO.getPk_currtype());
			// getBillCardPanel().setHeadItem("ncurrrate",
			// subscVO.getNcurrrate());
			getBillCardPanel().setHeadItem("ncurrrate", getCy().getRate(subscVO.getPk_currtype(), getClientEnvironment().getDate().toString()));
			getBillCardPanel().setHeadItem("pk_paymode", subscVO.getPk_paymode());
			getBillCardPanel().setHeadItem("subpk_paymode",subscVO.getPk_paymode());
			getBillCardPanel().setHeadItem("pk_lastbill", subscVO.getPrimaryKey());
			getBillCardPanel().setHeadItem("vlastbill", IPsDataMapping.SO_SUBSCRIBE);
			getBillCardPanel().setHeadItem("vlastbillcode", subscVO.getVbillno());
			getBillCardPanel().setHeadItem("reserve5", subscVO.getTs());
			getBillCardPanel().setHeadItem("ts", subscVO.getTs());
			if (isMakeChangeArea(subscVO.getPk_house(), subscVO.getDmakedate())) {
				execPriceInfoFormulas();
			} else {
				getBillCardPanel().setHeadItem("nypricebeforedis", subscVO.getNypricebeforedis());
				getBillCardPanel().setHeadItem("nytotalbeforedis", subscVO.getNytotalbeforedis());
				getBillCardPanel().setHeadItem("npricebeforedis", subscVO.getNpricebeforedis());
				getBillCardPanel().setHeadItem("ntotalbeforedis", subscVO.getNtotalbeforedis());
				getBillCardPanel().setHeadItem("nsignarea", subscVO.getNsalearea());
				getBillCardPanel().setHeadItem("sellarea", subscVO.getNsalearea());
				getBillCardPanel().setHeadItem("pk_project", subscVO.getPk_project());

			}
			getBillCardPanel().setHeadItem("nsignarea", subscVO.getNsalearea());
			getBillCardPanel().setHeadItem("pk_fmcrit", subscVO.getPk_fmcrit());
			getBillCardPanel().setHeadItem("nfmprice", subscVO.getNfmprice());
			getBillCardPanel().setHeadItem("nyfmprice", subscVO.getNyfmprice());
			getBillCardPanel().setHeadItem("nftundismny", subscVO.getNftundismny());
			getBillCardPanel().setHeadItem("nftdismny", subscVO.getNftdismny());
			getBillCardPanel().setHeadItem("nftcontmny", subscVO.getNftcontmny());
			getBillCardPanel().setHeadItem("bisfmcrit", subscVO.getBisfmcrit());
			getBillCardPanel().setHeadItem("pk_bank", subscVO.getPk_bank());
			getBillCardPanel().setHeadItem("bispresent", subscVO.getBispresent());
			getBillCardPanel().setHeadItem("bisdmltn", subscVO.getBisdmltn());
			getBillCardPanel().setHeadItem("pk_dmltnsite", subscVO.getPk_dmltnsite());

			//add by chixy 2011-01-13 �����Ϲ����ۺ󵥼ۺ��ܼ�
			getBillCardPanel().setHeadItem("nypricesign", subscVO.getNypriceafterdis());
			getBillCardPanel().setHeadItem("npricesign", subscVO.getNpriceafterdis());
			getBillCardPanel().setHeadItem("nytotalmnysign", subscVO.getNytotalafterdis());
			getBillCardPanel().setHeadItem("ntotalmnysign", subscVO.getNtotalafterdis());

			// ������
			updateShowDigitsByCard();
			PSBusinessDelegator delegator = new PSBusinessDelegator();
			SoSubscDiscountVO[] subDiscount = (SoSubscDiscountVO[]) delegator.queryByCondition(SoSubscDiscountVO.class, " pk_subsc='" + subscVO.getPrimaryKey()
					+ "' ");
			if (subDiscount != null && subDiscount.length > 0) {
				for (int i = 0; i < subDiscount.length; i++) {
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).addLine();
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).setValueAt(subDiscount[i].getPk_discount(), i, "pk_discount");
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).setValueAt(subDiscount[i].getReserve14(), i, "reserve14");
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).setValueAt(subDiscount[i].getReserve15(), i, "reserve15");
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).setValueAt(subDiscount[i].getFpriority(), i, "fpriority");
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).setValueAt(subDiscount[i].getNythisdiscountmny(), i, "nythisdiscountmny");
					getBillCardPanel().getBillModel(getSubscPageDiscountName()).setValueAt(subDiscount[i].getNyfitmentdiscountmny(), i, "nyfitmentdiscountmny");
				}
				getBillCardPanel().getBillModel(getSubscPageDiscountName()).execLoadFormula();
			}

		} else {
			execPriceInfoFormulas();
		}
		//remove by wangjiang at 20100513
		//getBillCardPanel().execHeadFormulas(new String[] { "pk_project->getColValue(crm_bd_house,pk_project,pk_house,pk_house)" });
		/**
		getBillCardPanel().setHeadItem("vbillno",getBillNo());
		// ��ͬ��
		getBillCardPanel().setHeadItem("vbargainnum", getBillCardPanel().getHeadItem("vbillno").getValueObject());
		*/
		getBillCardPanel().getHeadItem("bisdmltn").setEnabled(true);
		stateChanged(null);


	}

	/**
	 * ��ʼ���۸���������
	 */
	public void execPriceInfoFormulas() {
		//modify by wangjiang at 20100513
		String pk_house = getBillCardPanel().getHeadItem(getPk_house_Name()).getValueObject()!=null?getBillCardPanel().getHeadItem(getPk_house_Name()).getValueObject().toString() : null;
		if (pk_house != null && pk_house.trim().length() > 0){
			try{
				HashMap<String, Object> hash = getISellctrl().clickSigningBtnGetHouseInfo(pk_house);
				BdHouseVO houseVO = (BdHouseVO)hash.get("houseVO");
				if (houseVO != null){
					getBillCardPanel().setHeadItem("sellarea", houseVO.getNsellarea());
					getBillCardPanel().setHeadItem("nsignarea", houseVO.getNsellarea());
					getBillCardPanel().setHeadItem("pk_project", houseVO.getPk_project());
				}
				BdPriceVO priceVO = (BdPriceVO)hash.get("priceVO");
				if (priceVO != null){
					getBillCardPanel().setHeadItem("pk_price", priceVO.getPrimaryKey());
					getBillCardPanel().setHeadItem("nypricebeforedis", priceVO.getNyprice());
					getBillCardPanel().setHeadItem("nytotalbeforedis", priceVO.getNymny());
				}
			}
			catch(BusinessException ex){
				getBillCardPanel().execHeadFormulas(
						new String[] { "pk_price->getColValue(crm_bd_price, pk_price, pk_house, pk_house);",
								"pk_project->getColValue(crm_bd_house,pk_project,pk_house,pk_house)",
								"sellarea->getColValue(crm_bd_house, nsellarea, pk_house, pk_house);",
								"nsignarea->getColValue(crm_bd_house, nsellarea, pk_house, pk_house);",
								"nypricebeforedis->getColValue(crm_bd_price, nyprice, pk_price, pk_price);",
						"nytotalbeforedis->getColValue(crm_bd_price, nymny, pk_price, pk_price);" });
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doAddAction(ILinkAddData adddata) {

		super.doAddAction(adddata);
		setHeadEditItem();
		if(IPSModuleCode.CUSTOMERCENTER.equals(adddata.getSourceBillType())){

		}else{
			try {
				// ((ClientEventHandler) getManageEventHandler()).handlingPrice(
				// getBillCardPanel().getHeadItem("nypricebeforedis")
				// .getValueObject(), getBillCardPanel().getHeadItem(
				// "nytotalbeforedis").getValueObject());
				HashMap map = (HashMap)adddata.getUserObject();
				String nodeType = (String)map.get("nodeType");
				if (ISellStatus.H_SUBSCRIBE.equals(nodeType)) {
//				setMortgageValues(true, getHeadItemString("fpmtype"));
					String pmtype = getHeadItemString("fpmtype");
					if(pmtype!=null && "0".equals(pmtype))
						setMortgage(Integer.valueOf(pmtype),true);
					else
						getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, false);

					//	�Ϲ�תǩԼ���ͻ�ҳǩ�����пͻ�����ϱ�ʶ�����Ϲ�����Щ�ͻ������޸ġ�ɾ��
					BillModel bmcus = getBillCardWrapper().getBillCardPanel().getBillModel("ps_so_sign_customer");
					for (int i = 0; i < bmcus.getRowCount(); i++) {
						bmcus.setValueAt(new UFBoolean(true), i, "bfromsubsc");
					}
				}
			} catch (Exception e) {
				nc.bs.logging.Logger.error(e.getMessage(), e);
			}
		}
		//getBillCardPanel().execHeadFormulas(new String[] { "dputhousedate->getColValue(crm_bd_building, dlivingdate, pk_building, pk_building)" });
		appendagestatechange(true);
		if(SafeObject.isNotNull(getBillCardPanel().getHeadItem("pk_project").getValueObject())){
			//��������Ŀ����
			setPk_project((String) getBillCardPanel().getHeadItem("pk_project").getValueObject());
		}else{
			PSOpenNodePubData tmp = (PSOpenNodePubData)adddata;
			HashMap tmpObj =(HashMap) tmp.getUserObject();
			String pk_project = (String) tmpObj.get("pk_project");
			if(SafeObject.isNotNull(pk_project)){
				getBillCardPanel().setHeadItem("pk_project",pk_project);
				//��������Ŀ����
				setPk_project(pk_project);
			}
		}
		//�ź�תǩԼʱ���źŵ���Ч��Χû��ѡ�������������ɱ༭
		if(SafeObject.isNull(getBillCardPanel().getHeadItem("pk_house").getValueObject())){
			getBillCardPanel().getHeadItem("pk_house").setEnabled(true);
		}
//		getBillCardPanel().getBodyTabbedPane().setEnabledAt(7, false);
//	    getBillCardPanel().getBillModel("ps_so_sign_mortgage").getItemByKey("def_mgg_code").setEnabled(false);

//		try {
//			setMortgage(true);
//		} catch (Exception e) {
//			nc.bs.logging.Logger.error(e.getMessage(), e);
//		}

//		Object lastbillObj = getBillCardPanel().getHeadItem("pk_lastbill").getValueObject();
//		if (lastbillObj != null && lastbillObj.toString().length() > 0) {
//			Object dsigndate = getBillCardPanel().getHeadItem("dsigndate").getValueObject();
//			if(dsigndate != null && dsigndate.toString().length() > 0){
//				((ClientEventHandler) getManageEventHandler()).setDsignDate(new UFDate(dsigndate.toString()));
//			}
//		}

	}

	@Override
	public void doMaintainAction(ILinkMaintainData maintaindata) {

		super.doMaintainAction(maintaindata);
		try {
			updateBtnStateByCurrentVO();
		} catch (Exception e) {

		}
	}

	/**
	 * @��������: ���ð�������ҳǩ
	 * @���ߣ��ų� ʱ�䣺2009-11-18 ����09:55:12
	 * @param  type  �Ƿ�ȡ������������(0-ȡ,1-��ȡ)
	 * @param  tag   ��ʾ�������Ϸ�Χ(true-��ʾ�Ϲ����а������ϣ�false-��ʾ��ǰ��˾�����а�������)
	 */
	public void setMortgage(int type, boolean  tag) throws Exception {
		pk_lastbill = "";
		SoSignMortgageVO[] soVos = null;
		if (type == 0){
			if (tag) {
				pk_lastbill = (String)getBillCardPanel().getHeadItem("pk_lastbill").getValueObject();
				soVos = getSignMortgageVos(nc.ui.ps.ps3525.MortgageGetter.getInstance().getMortgages(pk_lastbill),"pk_mortgage");
			} else {
				soVos = getSignMortgageVos( MortgageGetter.getInstance().getMortgages(tag, null),"pk_mortgage");
			}
		}

		if (soVos != null && soVos.length>0){
			getBillCardWrapper().getBillCardPanel().getBillModel("ps_so_sign_mortgage").setBodyDataVO(soVos);
		}
		getBillCardWrapper().getBillCardPanel().getBillModel("ps_so_sign_mortgage").execLoadFormula();
	}

	private SoSignMortgageVO[] getSignMortgageVos(SuperVO[] mortgages,String setVoCode) {
		SoSignMortgageVO[] tempVos = null;
        if (mortgages != null && mortgages.length>0){
        	tempVos = new SoSignMortgageVO[mortgages.length];
        	for (int i=0; i<mortgages.length; i++) {
        		tempVos[i] = new SoSignMortgageVO();
        		tempVos[i].setPk_mortgage((String)mortgages[i].getAttributeValue(setVoCode));
        	}
        }
		return tempVos;
	}

	/**
	 * @����ʱ�䣺2007-4-28 ����11:04:38
	 * @return oPEREATE
	 */
	public Integer getOPEREATE() {
		return OPEREATE;
	}

	/**
	 * @����ʱ�䣺2007-4-28 ����11:04:38
	 * @param opereate
	 *        Ҫ���õ� oPEREATE
	 */
	public void setOPEREATE(Integer opereate) {
		OPEREATE = opereate;
	}

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	/**
	 * ԭ���ֶ�����
	 */
	public String getOriginCurrFldName() {
		return "pk_currtype";
	}

	/**
	 * ��ͷԭ�ҽ���ֶ�
	 */
	public String[] getHeadOriginItems() {
		return new String[] {
		// "nypricesign",
				"nytotalmnysign",
				//"nypricebeforedis",
				"nytotalbeforedis",
				//"nyfmprice",
				"nyprojectmny" };
		// return null;
	}

	/**
	 * ��ͷ���ҽ���ֶ�
	 */
	public String[] getHeadBaseItems() {
		return new String[] { //"npricebeforedis",
				"ntotalbeforedis",
		// "npricesign",
				"ntotalmnysign",
				//"nfmprice",
				"nprojectmny" };
		// return null;
	}

	/**
	 * ����ԭ�ҽ���ֶ�(֧�ֶ�ҳǩ)
	 */
	public String[][] getBodyOriginItems() {
		return new String[][] { null,// null,
				// {"nyshouldmny","nyfactmny"},
				{ // "ndiscountrate",//�ۿ�
				// "nthisdiscountrate",//�����ۿ�
				"nythisdiscountmny" }, null, null, null, null, null, null };
		// return null;
	}

	/**
	 * ���屾�ҽ���ֶ�(֧�ֶ�ҳǩ)
	 */
	public String[][] getBodyBaseItems() {
		return new String[][] { null,// null,
				// { "nshouldmny","nfactmny"},
				{ "nthisdiscountmny" }, null, null, null, null, null, null };
		// return null;
	}

	/**
	 * ���屾�һ����ֶ�(֧�ֶ�ҳǩ) ֻ��Ҫһ���ֶΣ�����Ϊһά�����㹻 created by chenliang at 2007-9-20 ����04:43:29
	 * @return
	 */
	public String[] getBodyRateItems() {
		return new String[] { "", "", "ncurrrate", "ncurrrate", "", "", "ncurrrate", "" };
	}

	/**
	 * ������ʾ���� created by chenliang at 2007-9-18 ����08:32:36
	 */
	protected void updateShowDigits() {
		super.updateShowDigits();
		// ������⴦����Ա������ҳǩ�����⴦��
		updateSpecialDigits();
	}

	protected void updateSpecialDigits() {
		getBillCardPanel().getBillData().getBodyItem(getCoursePageSign(), "nyshouldmny").setDecimalDigits(new Integer(getDigitOrigin()).intValue());
		getBillCardPanel().getBillData().getBodyItem(getCoursePageSign(), "nyfactmny").setDecimalDigits(new Integer(getDigitOrigin()).intValue());
		getBillCardPanel().getBillData().getBodyItem(getCoursePageSign(), "nshouldmny").setDecimalDigits(new Integer(getDigitBase()).intValue());
		getBillCardPanel().getBillData().getBodyItem(getCoursePageSign(), "nfactmny").setDecimalDigits(new Integer(getDigitBase()).intValue());
		if (isListPanelSelected()) {
			getBillListPanel().getBodyItem(getCoursePageSign(), "nyshouldmny").setDecimalDigits(new Integer(getDigitOrigin()).intValue());
			getBillListPanel().getBodyItem(getCoursePageSign(), "nyfactmny").setDecimalDigits(new Integer(getDigitOrigin()).intValue());
			getBillListPanel().getBodyItem(getCoursePageSign(), "nshouldmny").setDecimalDigits(new Integer(getDigitBase()).intValue());
			getBillListPanel().getBodyItem(getCoursePageSign(), "nfactmny").setDecimalDigits(new Integer(getDigitBase()).intValue());

		}
	}

	// ���ݱ���װ�ޱ�׼�������ͷ���װ�޵���
	public void setHeadFitment() {
		int rowcount = getBillCardPanel().getBillModel("ps_so_sign_fitment").getRowCount();
		Double fitmentmny = 0.0;
		for (int i = 0; i < rowcount; i++) {
			if (getBillCardPanel().getBodyValueAt(i, "vcode") != null && getBillCardPanel().getBodyValueAt(i, "nprice") != null) {
				fitmentmny = fitmentmny + Double.parseDouble(getBillCardPanel().getBodyValueAt(i, "nprice").toString());
			}
		}
		// ��ͷ�����µ�ʱ�򵥼�
		getBillCardPanel().setHeadItem("nyfmprice", fitmentmny);
		// װ�޿���ǰ���Ϊװ�޵���*���
		Double fitmentarea = Double.parseDouble(getBillCardPanel().getHeadItem("nsignarea").getValueObject().toString());
		getBillCardPanel().setHeadItem("nftundismny", fitmentarea * fitmentmny);
		// װ���ۺ���Ϊ��
		getBillCardPanel().setHeadItem("nftdismny", null);
	}

	//	���ڸ��������
	public void setheaditemenabled(boolean benabled) {
		getButtonManager().getButton(IPSButton.CreateCourseBtn).setEnabled(benabled);
		updateButtons();

		getBillCardPanel().getHeadItem("vbargainnum").setEnabled(benabled);
		getBillCardPanel().getHeadItem("vrecordbargainnum").setEnabled(benabled);
		getBillCardPanel().getHeadItem("dsigndate").setEnabled(benabled);
//		getBillCardPanel().getHeadItem("pk_paymode").setEdit(benabled);
		getBillCardPanel().getHeadItem("dputhousedate").setEnabled(benabled);
		getBillCardPanel().getHeadItem("pk_bank").setEnabled(benabled);
		getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(benabled);
		getBillCardPanel().getHeadItem("pk_deptdoc").setEnabled(benabled);
		getBillCardPanel().getHeadItem("bispresent").setEnabled(benabled);
		getBillCardPanel().getHeadItem("vmemo").setEnabled(benabled);
	}

	//	����ǩԼ���ϲ���
	public void setheaditmenabled(boolean benabled) {
		getButtonManager().getButton(IPSButton.CreateCourseBtn).setEnabled(benabled);
		updateButtons();

		for (BillItem billitem : getBillCardPanel().getHeadItems()) {
			if (billitem.isNull()) {
				billitem.setEnabled(benabled);
			}
		}
		getBillCardPanel().getHeadItem("pk_fmcrit").setEnabled(benabled);
		getBillCardPanel().getHeadItem("bispresent").setEnabled(benabled);
	}

	public boolean isPayModeChange() {
		return payModeChange;
	}

	public void setPayModeChange(boolean payModeChange) {
		this.payModeChange = payModeChange;
	}

	public boolean isFitmentChange() {
		return fitmentChange;
	}

	public void setFitmentChange(boolean fitmentChange) {
		this.fitmentChange = fitmentChange;
	}

	public boolean getBisappendagechg() {
		return bisappendagechg;
	}

	public void setBisappendagechg(boolean bisappendagechg) {
		this.bisappendagechg = bisappendagechg;
	}

	public boolean isBiscontractchg() {
		return biscontractchg;
	}

	public void setBiscontractchg(boolean biscontractchg) {
		this.biscontractchg = biscontractchg;
	}

	public boolean isFitmenterrFlag() {
		return fitmenterrFlag;
	}

	public void setFitmenterrFlag(boolean fitmenterrFlag) {
		this.fitmenterrFlag = fitmenterrFlag;
	}

	public SoCourseBVO[] getOldcourseBVOs() {
		return oldcourseBVOs;
	}

	public void setOldcourseBVOs(SoCourseBVO[] oldcourseBVOs) {
		this.oldcourseBVOs = oldcourseBVOs;
	}

	public boolean isBtranstomem() {
		return btranstomem;
	}

	public void setBtranstomem(boolean btranstomem) {
		this.btranstomem = btranstomem;
	}

	public String[] getPk_customers() {
		return pk_customers;
	}

	public void setPk_customers(String[] pk_customers) {
		this.pk_customers = pk_customers;
	}

	@Override
	public List<String> getBufferMaintainItems() {
		List<String> list = new ArrayList<String>();
		list.add("subpk_paymode");
		return list;
	}

	@Override
	public String[] getAutoAddLineTabCodes() {
		return new String[]{"ps_so_sign_customer"};
	}

	public FDCBodyImgBtn[] getBodyImgBtns(String tableCode) {
		if(tableCode!=null && tableCode.equals("ps_so_sign_customer")){
			FDCBodyImgBtn[] btns = new FDCBodyImgBtn[]{
					getAddCusBtn()
			};
			return btns;
		}
		return super.getBodyImgBtns(tableCode);
	}

	private QuickAddCusImgBtn addCusBtn = null;
	private QuickAddCusImgBtn getAddCusBtn(){
		if(addCusBtn==null){
			addCusBtn = new QuickAddCusImgBtn(this);
		}
		return addCusBtn;
	}

	public void fillNewData(String dataPk) {

		BillModel bm = getBillCardPanel().getBillModel("ps_so_sign_customer");
		CustomerUtil.fillCustBodyModelData(bm, dataPk);

	}

	public IRefLinkAddData getRefLinkAddData() {

		ButtonObject addlineBtn = getButtonManager().getButton(IBillButton.AddLine);
		if(!getBillCardPanel().getBillModel(getBillCardPanel().getCurrentBodyTableCode()).isEnabled()
				||addlineBtn==null || !addlineBtn.isEnabled()){
			MessageDialog.showErrorDlg(this, NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-003035")//@res "��ǰ�޷����������ͻ���"
);
			return null;
		}

		FdcPubRefLinkAddData linkAddData = FdcPubRefLinkAddData.getInstance();
		linkAddData.setBillFillNewData(this);

		Map<String, String> otherDataMap = new HashMap<String, String>();
		otherDataMap.put("pk_project", getHeadItemString("pk_project"));
		linkAddData.setSourceFunCode(_getModuleCode());
		linkAddData.setOtherData(otherDataMap);

		return linkAddData;
	}

	@Override
	public SoCourseBVO[] getHouseCourse(String pk_house) {
		if(getBillOperate()==IBillOperate.OP_ADD
				||getBillOperate()==IBillOperate.OP_REFADD){
			return super.getHouseCourse(pk_house);
		}
		IPSBusiness service = NCLocator.getInstance().lookup(IPSBusiness.class);
		AggregatedValueObject billVO = getBufferData().getCurrentVO();
		if(billVO!=null && billVO.getParentVO()!=null){
			try {
				String pk_bill = billVO.getParentVO().getPrimaryKey();
				if(!courseBVOSMap.containsKey(pk_bill)){
					SoCourseBVO[] vos = service.getSignOrSubscCourse(pk_house, pk_bill);
					if(vos!=null&&vos.length>0){
						courseBVOSMap.put(pk_bill,vos);
					}
				}
				return courseBVOSMap.get(pk_bill);
			} catch (Exception e) {
				Logger.error(e, e);
			}
		}else{
			return super.getHouseCourse(pk_house);
		}
		return null;
	}

	@Override
	protected int getExtendStatus(AggregatedValueObject vo) {
		int billstatus = 0;
		if(vo == null||vo.getParentVO()==null)
			return billstatus;
		if(getBillOperate()==IBillOperate.OP_ADD
				||getBillOperate()==IBillOperate.OP_EDIT
				||getBillOperate()==IBillOperate.OP_REFADD)
			return billstatus;
		if(PsCommonUtil.notNull(vo.getParentVO().getAttributeValue("vbillstatus"))
				&&String.valueOf(IBillStatus.CHECKPASS).equals(vo.getParentVO().getAttributeValue("vbillstatus").toString())
				&&!PsCommonUtil.notNull(vo.getParentVO().getAttributeValue("reserve1"))){
			return btnState_effectiveApprove;
		}
		return super.getExtendStatus(vo);
	}

	public Map<String, SoCourseBVO[]> getCourseBVOSMap() {
		return courseBVOSMap;
	}

	public void setCourseBVOSMap(Map<String, SoCourseBVO[]> courseBVOSMap) {
		this.courseBVOSMap = courseBVOSMap;
	}

	@Override
	protected void setTotalUIState(int intOpType) throws Exception {
		super.setTotalUIState(intOpType);
		try {
			//������չ״̬
			getButtonManager().setButtonByextendStatus(getExtendStatus(getBufferData().getCurrentVO()));
			updateButtons();
		} catch (Exception e) {
			Logger.error("��ʼ����ť�����쳣��", e);
		}
	}

}