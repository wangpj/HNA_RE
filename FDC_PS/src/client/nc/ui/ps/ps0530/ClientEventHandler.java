/**
 *
 */
package nc.ui.ps.ps0530;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ml.NCLangRes4VoTransl;
import java.util.HashMap;
import nc.bs.framework.common.NCLocator;
import nc.itf.ps.pub.IBillCodeBusi;
import nc.itf.ps.pub.IDownList;
import nc.itf.ps.pub.IPsDataMapping;
import nc.ui.ps.pub.addline.AutoAddLineUtil;
import nc.ui.ps.pub.bsdelegate.PSBDBusinessDelegator;
import nc.ui.ps.pub.eventhandler.ManageEventHandler;
import nc.ui.ps.pub.ui.BillManageUI;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillModel;
import nc.ui.trade.base.IBillOperate;
import nc.ui.trade.bill.BillTemplateWrapper;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.trade.controller.IControllerBase;
import nc.uif.pub.exception.UifException;
import nc.vo.fdc.fdc0102.BdOperstateVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.ps.ps0530.BdPaymodeBVO;
import nc.vo.ps.ps0530.BdPaymodeVO;
import nc.vo.ps.ps0535.BdFundtypeVO;
import nc.vo.ps.ps0540.BdFundsetVO;
import nc.vo.ps.pub.button.IPSButton;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billcodemanage.BillCodeObjValueVO;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author ssd
 *
 */
public class ClientEventHandler extends ManageEventHandler {

	/**
	 * @param billUI
	 * @param control
	 */
	public ClientEventHandler(BillManageUI billUI, IControllerBase control) {
		super(billUI, control);
		// TODO 自动生成构造函数存根!
	}
	@SuppressWarnings("unchecked")
	protected void save()throws Exception	{
		int count=getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object fpmtypeOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("fpmtype").getValueObject()==null?"0":getBillCardPanelWrapper().getBillCardPanel().getHeadItem("fpmtype").getValueObject();
		String fpmtype = fpmtypeOBJ.toString();
		try {
			BdPaymodeBVO[] payModel=(BdPaymodeBVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(BdPaymodeBVO.class.getName());
			int unitnum=0;//记录舍入方式为：接受尾差的数目，如果多于1个，则报错！
			if(payModel !=null &&payModel.length>=1){
				for(BdPaymodeBVO tempvo:payModel){
					if(tempvo!=null&&tempvo.getFchoice()!=null && tempvo.getFchoice().intValue()==3){
						unitnum++;
					}
				}

				if(unitnum>1){
					MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002954")//@res "只能有一个款项进程的舍入方式为：接受尾差！"

);
					return ;
				}
				if(unitnum==0){
					if("2".equals(fpmtype)){ //赠房类进程无款项，所以无需校验！
						//do nothing
					}else{
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
								, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002955")//@res "款项进程中，必须有一个进程的舍入方式设置为：接受尾差！"

						);
						return ;
					}
				}
			}
			for(int i=0;i<count;i++)
			{
				Integer intfpmcourse = payModel[i].getFpmcourse();  //购房进程
				Integer intffunction = payModel[i].getFfunction();	//功能点
				if(i ==0){

					/**第一行的数据!*/
					Object objSpacing = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ispacing");//日期间隔
					Object objFinish = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "dfinishdate");//固定日期
//					String fpmcourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fpmcourse")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fpmcourse").toString();//购房进程
//					String ffunction=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ffunction")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ffunction").toString();//功能点
					String fbfcourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fbfcourse")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fbfcourse").toString();//前置进程
					if(intfpmcourse==null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000050")//@res "购房进程不能为空"
);
						return ;
					}
					//认购 //签约
					if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SUBSCRIBE){
						if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SIGNING){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000052")//@res "第一行的购房进程必须是认购或者签约"
);
							return ;
						}
					}

					/**功能点 */
					if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SUBSCRIBE){
						if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SIGNING){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000056")//@res "第一行的功能点必须是认购或者签约"
);
							return ;
						}
					}
					if(fbfcourse !=null && fbfcourse.length()>0){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000057")//@res "第一行没有前置进程"
);
						return ;
					}

					if(intffunction==null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000051")//@res "功能点不能为空"
);
						return ;
					}

					if(objSpacing !=null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000053")//@res "第一行不应该填写日期间隔"
);
						return ;
					}
					if(objFinish !=null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000054")//@res "第一行不应该填写固定日期"
);
						return ;
					}

					boolean hasSign=false;
					if(payModel !=null &&payModel.length>=1){
						for(BdPaymodeBVO tempvo:payModel){
							if(tempvo!=null&&tempvo.getFpmcourse()!=null&&tempvo.getFpmcourse().intValue()==IPsDataMapping.SELL_COURSE_SIGNING){
								hasSign=true;
							}

						}
						if(hasSign==false){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000055")//@res "付款方式必须包含一个'签约'进程"
);
							return ;
						}
					}

					String bisfunds=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund").toString();
					if(bisfunds !=null && !bisfunds.equals(""))
					{
						if(new UFBoolean(bisfunds).booleanValue()){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000058")//@res "第一行购房进程为认购或者是签约时不能选中是否款项,请取消是否款项"
);
							return ;
						}
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "faccount");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfscode1");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "paymode");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "pk_fundset_fund");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "pk_fundset_else");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "npmnum");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "ffundset_else");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "fchoice");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vanjietype");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfsname");
					}
				}else{
					Object objSpacing = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ispacing");//日期间隔
					Object objFinish = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "dfinishdate");//固定日期
//					Object objFpmcourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fpmcourse");//购房进程,取值为汉字，大定金等
//					Object ffunction=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ffunction");//功能点
//					Object vfscode1OBJ=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "vfscode1");//款项
					Object fundset = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "pk_fundset_fund");
					UIRefPane vfscode1OBJ=(UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getBodyItem("vfscode1").getComponent();//款项
					String bisfund=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund").toString();
					Boolean fund=Boolean.parseBoolean(bisfund);
					int row=i+1;
					if(intffunction ==null ){
						if(fund==false){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000060")//@res "行,功能点或款项请填写其中一个"
);
							return ;
						}
					}
					if(intfpmcourse!=null &&(intfpmcourse==IPsDataMapping.SELL_COURSE_LOAN_UP || intfpmcourse==IPsDataMapping.SELL_COURSE_GJJLOAN_UP)){
						vfscode1OBJ.setPK(fundset);
						if(vfscode1OBJ!=null&&(!vfscode1OBJ.getRefCode().equals("0104")&&!vfscode1OBJ.getRefCode().equals("0202")))  //按揭款或车位按揭款
						{
//							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
//									, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002805")//@res "商业（公积金）按揭到账进程的款项应为按揭款"
//									);
//							return ;
						}

					}

					//如果第一行购房进程的数据不是签约,那么以后的行必须有签约进程
					boolean hasSign=false;
					boolean hasfund=false;
					HashMap ffundMap=new HashMap();
					HashMap <Integer,Integer>fpmcoursemap=new HashMap<Integer,Integer>();
					if(payModel !=null &&payModel.length>=1){
						for(BdPaymodeBVO tempvo:payModel){
							if(tempvo!=null&&tempvo.getFpmcourse()!=null&&tempvo.getFpmcourse().intValue()==IPsDataMapping.SELL_COURSE_SIGNING){
								hasSign=true;
							}
							if(tempvo!=null&&tempvo.getBisfund()!=null&&tempvo.getBisfund().booleanValue()){
								if(tempvo.getFaccount()==null||tempvo.getPk_fundset_fund()==null||tempvo.getNpmnum()==null||tempvo.getPk_fundset_else()==null||tempvo.getFchoice()==null){
									hasfund=true;
								}
							}
							if(tempvo !=null &&tempvo.getFfunction() !=null && !tempvo.equals(""))
							{
								if(tempvo.getFfunction()!=3 && ffundMap.containsKey(tempvo.getFfunction()))
								{
									MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000061")//@res "功能点重复"
);
									return ;
								}else{
									ffundMap.put(tempvo.getFfunction(), tempvo.getFfunction());
								}
							}
							//将所选择的进程号存入fpmcoursemap中，以便按揭手续和按揭到账的约束做基础。
							if(tempvo!=null&&tempvo.getFpmcourse()!=null&&tempvo.getFpmcourse().intValue()>0){
								fpmcoursemap.put(tempvo.getFpmcourse(),tempvo.getFpmcourse());
							}
						}
					}
					//如进程中选择商业按揭手续，则必须选择商业按揭到账，反之，相同，即成对出现；同理，公积金按揭手续和公积金到账
					//这4个选项中，商业按揭手续对应的数值最小，为14;商业按揭到帐=16;公积金按揭手续=22;公积金按揭到帐=23.
					if( ( fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_PROCEDURE)&&!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_UP))
						  ||(!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_PROCEDURE)&&fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_UP))
					  )
					{
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
								, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002771")//@res "进程中应同时包含商业按揭手续和商业按揭到账"
						);
						return ;
					}

					if( ( fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_PROCEDURE)&&!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_UP))
							  ||(!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_PROCEDURE)&&fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_UP))
						  )
						{
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
									, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002772")//@res "进程中应同时包含公积金按揭手续和公积金按揭到账"
							);
							return ;
						}

					if(hasSign==false){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000055")//@res "付款方式必须包含一个'签约'进程"
);
						return ;
					}
					if(hasfund==true){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000062")//@res "如果选中了是否款项,则应该输入后续项(计算方式,款项,数值,款项另属于,舍入方式)"!
);
						return ;
					}


					if(intfpmcourse!=null &&( intfpmcourse==IPsDataMapping.SELL_COURSE_SUBSCRIBE || intfpmcourse==IPsDataMapping.SELL_COURSE_SIGNING)){

						if(fund!=null)
						{
							if(fund){
								MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000063")//@res "当购房进程为认购或者是签约时不能选中是否款项,请取消是否款项"
);
								return ;
							}
						}else{
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "faccount");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfscode1");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "paymode");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "pk_fundset_fund");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "pk_fundset_else");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "npmnum");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "ffundset_else");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "fchoice");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vanjietype");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfsname");
						}
					}

					if(fund !=null){
						if(fund){
							/**前置进程 */
							if(i >=1){
								if((objSpacing==null || objSpacing.toString().trim().equals("")) && (objFinish==null || objFinish.toString().trim().equals(""))){
									MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000064")//@res "行的日期间隔或者固定日期必须填写其中一个"
);
									return ;
								}
								if((objSpacing!=null)&&(objFinish!=null)){
									MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
,NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000065")//@res "行的日期间隔或者固定日期只能填写其中一个"
);
									return ;
								}
							}

							if(intffunction!=null) {
								MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000066")//@res "是否款项若为(是)，则本次步骤没有对应功能点"
);
								return ;
							}
							//}
						}else{
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "faccount");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfscode1");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "paymode");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "pk_fundset_fund");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "pk_fundset_else");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "npmnum");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "ffundset_else");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "fchoice");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vanjietype");
							getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfsname");
						}

					}

					if((objSpacing==null || objSpacing.toString().trim().equals("")) && (objFinish==null || objFinish.toString().trim().equals("")))
					{
						MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000064")//@res "行的日期间隔或者固定日期必须填写其中一个"
);
						return ;
					}
					if((objSpacing!=null)&&(objFinish!=null))
					{
						MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
,NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000065")//@res "行的日期间隔或者固定日期只能填写其中一个"
);
						return ;

					}

				}


			}

		}catch(Exception e)	{
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		//根据表头的业态判断表体的款项
		if(bisposition().booleanValue()==false){
			return ;
		}

//		//如果方式编码为空 则自动生成编码 EDIT guanyj
//		Object vpmcodeObj = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").getValueObject();
//		if (vpmcodeObj == null || vpmcodeObj.toString().trim().equals("")) {
//			getBillCardPanelWrapper().getBillCardPanel().setHeadItem(
//					getBillNoFieldCode(), ((ClientUI)getBillUI()).getBillNo());
//		}

		super.onBoSave();

	}

	/**
	 *
	 * @throws Exception
	 * @作者 孙胜东
	 * @创建时间：2007-9-20 上午09:01:52
	 * @说明：判断表头的业态，如果是车位，那么款项表体的所有款项也必须都是车位款，如果不是车位则必须都是房款
	 */
	private Boolean bisposition() throws Exception{
		BdPaymodeVO paymodeVOObj=(BdPaymodeVO) getBillUI().getVOFromUI().getParentVO();
		if(paymodeVOObj.getPk_situation() ==null||paymodeVOObj.getPk_situation().trim().length()==0){
			return true;
		}
		HashMap postMap=bispostMap();//断断是不是车位的HASHMAP,存放业态主键和对应的业态VO
		HashMap fundCodeMap=getFoundType();//判断款项类型的CODE
		BdOperstateVO stateObj=(BdOperstateVO) postMap.get(paymodeVOObj.getPk_situation());
		BdPaymodeBVO[] paymodeBVOs=(BdPaymodeBVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(BdPaymodeBVO.class.getName());

		//如果是业态是车位的处理操作
		if(stateObj.getBisposition() !=null &&(stateObj.getBisposition().booleanValue()==true|| stateObj.getBisposition().equals("Y"))){
			for(int i=0;i<paymodeBVOs.length;i++){
				UFBoolean fundBool=paymodeBVOs[i].getBisfund();
				if(fundBool !=null &&(fundBool.booleanValue()==true)){

					String curFoundType=fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund())==null?null:fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund()).toString();//款项类型的编码
					if(curFoundType !=null && !curFoundType.equals(IPsDataMapping.FUND_TYPE_CAR_MONEY)){
						int n=i+1;
						MessageDialog.showErrorDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+n+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000067")//@res "行的款项不是车位款"
);
						return false;
					}
				}
			}
		}else{
			//业态不是车位，则表体的款项必须都是房款
			for(int i=0;i<paymodeBVOs.length;i++){
				UFBoolean fundBool=paymodeBVOs[i].getBisfund();
				if(fundBool !=null &&(fundBool.booleanValue()==true)){

					String curFoundType=fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund())==null?null:fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund()).toString();//款项类型的编码
					if(curFoundType !=null && curFoundType.equals(IPsDataMapping.FUND_TYPE_CAR_MONEY)){
						int n=i+1;
						MessageDialog.showErrorDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "第"
+n+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000068")//@res "行的款项不是房款"
);
						return false;
					}
				}
			}
		}
		return true;
	}
	/**
	 *
	 * @作者 孙胜东
	 * @创建时间：2007-9-20 上午10:15:11
	 * @说明：返回HashMap
	 * 		  PK: 款项主键
	 * 		  值是:款项类型的编码CODE
	 */
	@SuppressWarnings("unchecked")
	private HashMap getFoundType() {
		BdFundtypeVO fundTypeVO = null;
		String returnStr = null;
		HashMap<String,String> fundTypeMap=new HashMap<String, String>();
		try {
			//查询款项设置.
			BdFundsetVO[] fundsetVOs=(BdFundsetVO[])HYPubBO_Client.queryByCondition(BdFundsetVO.class, " isnull(dr,0)=0 ");
            //查询款项设置对应的款型类型。
			for(BdFundsetVO setVOObj:fundsetVOs){
				fundTypeVO = (BdFundtypeVO) HYPubBO_Client.queryByPrimaryKey(BdFundtypeVO.class, setVOObj.getPk_fundtype());
				returnStr = fundTypeVO.getVftcode();
				fundTypeMap.put(setVOObj.getPrimaryKey(), returnStr);
			}

		} catch (UifException e) {
			// TODO 自动生成 catch 块
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return fundTypeMap;
	}

	private HashMap bispostMap()throws Exception{//存放业态主键和对应的业态VO
		HashMap<String,BdOperstateVO> stateMap=new HashMap<String, BdOperstateVO>();
		BdOperstateVO[] operstateObj=(BdOperstateVO[])getBusiDelegator().queryByCondition(BdOperstateVO.class, " isnull(dr,0)=0 ");
		if(operstateObj !=null&&operstateObj.length>=0){
			for(int i=0;i<operstateObj.length;i++){
				stateMap.put(operstateObj[i].getPrimaryKey(), operstateObj[i]);
			}
		}
		return stateMap;
	}

	protected void onBoSave() throws Exception{

		AutoAddLineUtil.delLineWhenSave(getBillUI());

		//添加比例总和的验证  edit by guanyj   款项另属会造成款项比例大于100% 故暂不修改20080329 14:33
//		int rowCount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
//		UFDouble sum = new UFDouble(0);
//		for(int i=0;i<rowCount;i++){
//			Object bisfundOBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund");
//			if (bisfundOBJ != null) {
//				String bisfund = bisfundOBJ.toString();
//				if ((new UFBoolean(bisfund)).booleanValue() && "比例(%)".equals(getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "faccount").toString())) {
//					sum = sum.add(new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "npmnum").toString()));
//				}
//			}
//		}
//		if(sum.doubleValue() > 100)
//			throw new BusinessException("付款比例总和大于100%！请检查比例数值！");
		//end by guanyj
		//验证：如果该付款方式选择了按揭，但下面款项进程中无按揭类款项，则报错。同样，如果选择了非按揭，款项进程中不能出现按揭类款项。guanyj
		Object fpmtypeOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("fpmtype").getValueObject();
		Object situationOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_situation").getValueObject();
		boolean iscarsituation=false;
		if(situationOBJ!=null&&situationOBJ.equals("0001Y51000000004ZRGL"))  //业态是否为车位
		{
			iscarsituation=true;
		}else{
			iscarsituation=false;
		}

		if(fpmtypeOBJ == null){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000069")//@res "表头 方式类型为空！"
);
		}
		String fpmtype = fpmtypeOBJ.toString();
		int rowCount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		boolean isHave=false;	//是否含有按揭款项
		boolean hasfund=false;	//是否含有款项
		boolean ischeweikuan=false; //是否含有车位款
		boolean iscaranjie=false;//是否含有车位按揭款
		int j=0,k=0,n=0;//j记录款项的数目，k记录车位款的数目,n记录车位按揭款的数目；
		for(int i=0;i<rowCount;i++){
			Object bisfundOBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund");
			if (bisfundOBJ != null) {
				String bisfund = bisfundOBJ.toString();
				if ((new UFBoolean(bisfund)).booleanValue()) {//是款项
					hasfund = true;
					if(i==0)
					{
						throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002835")//@res "第一个进程只能为认购或签约，不能选择款项！"
						);
					}
					j++;
					Object fundset = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "pk_fundset_fund");
					UIRefPane vfscode1OBJ=(UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getBodyItem("vfscode1").getComponent();//款项
					if(fundset == null){
						throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000070")//@res "表体第 "
								+(i+1)+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000071")//@res " 行：选择为款项，款项不能为空"
						);
					}
					vfscode1OBJ.setPK(fundset);
					if(vfscode1OBJ.getRefCode().equals("0104")){    //按揭款
						isHave=true;
					}

					if(vfscode1OBJ.getRefCode().equals("0202")){	//车位按揭款
						iscaranjie=true;
						n++;
					}

					if(vfscode1OBJ.getRefCode().equals("0201")){	//车位款
						ischeweikuan=true;
						k++;
					}
				}
			}
		}
/*		if(fpmtype.equals("0") && !isHave){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000073")//@res "按揭类款项进程中不能无按揭类款项！"
);
		}*/
		int carsitunum=n+k;
		if(fpmtype.equals("0")){
			if(!iscarsituation && (!isHave&&!iscaranjie))  //业态不是车位且不包含按揭款
			{
//				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000073")//@res "按揭类款项进程中不能无按揭类款项！"
//				);
			}
			if(iscarsituation && !iscaranjie) //业态为车位且不包含车位按揭款
			{
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-002856")//@res "业态为车位的按揭类进程应包含车位按揭款"
//@res "业态为车位的按揭类进程应包含车位按揭款！"
				);
			}

		}
/*		if(fpmtype.equals("1") && isHave){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000074")//@res "非按揭类款项进程中不能出现按揭类款项！"
);
		}*/
		if(fpmtype.equals("1")){
			if(!iscarsituation && (isHave||iscaranjie))  //业态不是车位且包含了按揭款
			{
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000074")//@res "非按揭类款项进程中不能出现按揭类款项！"
			   );
			}
			if(iscarsituation && iscaranjie) //业态为车位且包含了车位按揭款
			{
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-002857")//@res "业态为车位的非按揭类款项进程中不能出现车位按揭款！"
//@res "业态为车位的非按揭类款项进程中不能出现车位按揭款！"
				   );
			}
		}

		if (fpmtype.equals("2") && hasfund) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000075")//@res "赠房类进程中不能出现款项！"
);
		}
		if(!iscarsituation&&(ischeweikuan||iscaranjie)&&j!=carsitunum)//进程款项中包含车位款或车位按揭款，则所有款项进程的款项必须均为类型为车位款的款项（车位款或车位按揭款）.
		{
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002824")//@res "款项进程中包含车位款，则所有款项进程的款项必须均为车位款"
			);
		}
		save();



		getBillCardPanelWrapper().getBillCardPanel().getBillModel().sortByColumn("ipmnum", true);
	}


	@Override
	protected String getBillNoFieldCode() {
		return "vpmcode";
	}

	@Override
	protected void onBoDelete() throws Exception {
		// TODO 自动生成方法存根
/*		if (MessageDialog.showYesNoDlg(getBillUI(), "删除", "是否确认删除该数据?")
				!= UIDialog.ID_YES) {
				return;
			}*/
		super.onBoDelete();
	}
	public void onBoAdd(ButtonObject obj)throws Exception
	{
		super.onBoAdd(obj);
		((ClientUI)getBillUI()).afterProjectEdit();
		//新增时设置方式编码可编辑
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").setEnabled(true);
		editCard();
	}

	@Override
	protected void onBoQuery() throws Exception {
		String pk_corp=ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
		String querystr="(isnull(dr,0)=0) and pk_corp='0001'";
        if(pk_corp!=null&&pk_corp.equals("0001"))
        {
        	SuperVO[] queryVos = queryHeadVOs(querystr);
    		getBufferData().clear();
    		// 增加数据到Buffer
    		addDataToBuffer(queryVos);
    		updateBuffer();
        }else{
        	super.onBoQuery();
        }
	}

	/* （非 Javadoc）
	 * @see nc.ui.trade.manage.ManageEventHandler#onBoCopy()
	 */
	@Override
	protected void onBoCopy() throws Exception {
		// TODO 自动生成方法存根
		//清空界面数据
		super.onBoCopy();
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_project", null);
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_situation", null);
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_rebate", null);
		//getBillCardPanelWrapper().getBillCardPanel().setHeadItem("fpmtype", null);
		//getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vpmcode", null);
		//getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vpmname", null);
//		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vpmcode", ((ClientUI)getBillUI()).getBillNo());
		//设置方式编码可编辑 EDIT by gyj
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").setEnabled(true);
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("dmakedate", _getDate());
		String pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
		if(pk_corp.equals("0001"))
		{
			((ClientUI)getBillUI()).afterProjectEdit();
		}
		editCard();

	}

	@Override
	protected void onBoLineAdd() throws Exception {
		// TODO 自动生成方法存根
		super.onBoLineAdd();
		int selectRow = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(this._getCorp().getPrimaryKey(), selectRow, "pk_corp");
		int rowCount=getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		if( rowCount >0 ){
			for(int i=0;i<rowCount;i++)
			{
				int rowNum=i+1;
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(rowNum, i, "ipmnum");
			}
		}
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(selectRow+1, selectRow, "ipmnum");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
	}

	@Override
	protected void onBoLineIns() throws Exception {
		super.onBoLineIns();
		int selectRow = getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(selectRow+1, selectRow, "ipmnum");
		getBillCardPanelWrapper().getBillCardPanel().getBillModel().execLoadFormula();
		sortNum();
	}

	private void sortNum(){
		int rowC = getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowCount();
		for(int i=1;i<=rowC;i++){
			if(getBillCardPanelWrapper().getBillCardPanel().getBillModel().getRowState(i-1)==BillModel.NORMAL)
				getBillCardPanelWrapper().getBillCardPanel().getBillModel().setRowState(i-1, BillModel.MODIFICATION);
			getBillCardPanelWrapper().getBillCardPanel().getBillModel().setValueAt(i, i-1, "ipmnum");
		}
	}

	private void editCard() throws Exception{

		int count=getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		if(count >=0){
			if(count==0){
				String bisfunds=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(count, "bisfund")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(count, "bisfund").toString();
				if(bisfunds !=null && !bisfunds.equals(""))
				{
					if(new UFBoolean(bisfunds).booleanValue()){
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "vfscode1", true);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "faccount", true);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "npmnum", true);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "ffundset_else", true);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "fchoice", true);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "funits", true);
						//	按揭款才能选择按揭类型
						Object vfscode1OBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(count, "vfscode1");
						if(vfscode1OBJ == null){
							throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000070")//@res "表体第 "
									+(count+1)+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000071")//@res " 行：选择为款项，款项不能为空"
							);
						}
						String vfscode1 = vfscode1OBJ.toString();
						if(vfscode1.equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000072")//@res "按揭款"
)){
							//getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "vanjietype", true);
						}
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "ffunction", false);
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "ffunction");
					}else
					{
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "vfscode1", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "faccount", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "npmnum", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "ffundset_else", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "fchoice", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "vanjietype", false);
						getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(count, "ffunction", true);
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "faccount");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "vfscode1");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "npmnum");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "pk_fundset_else");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "ffundset_else");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "fchoice");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "funits");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "vanjietype");
						getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, count, "vfsname");

					}

				}

			}else{
					for(int i=0;i<count;i++){
						String bisfund=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund").toString();
						if(bisfund !=null && !bisfund.equals(""))
						{
							if(new UFBoolean(bisfund).booleanValue()){
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vfscode1", true);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "faccount", true);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "npmnum", true);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "ffundset_else", true);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "fchoice", true);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "funits", true);
//								按揭款才能选择按揭类型
								Object vfscode1OBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "vfscode1");
								if(vfscode1OBJ == null) {
									throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000070")//@res "表体第 "
											+(i+1)+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000071")//@res " 行：选择为款项，款项不能为空"
									);
								}
								String vfscode1 = vfscode1OBJ.toString();
								if(vfscode1.equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000072")//@res "按揭款"
								)){
									//getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vanjietype", true);
								}
							}else
							{
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vfscode1", false);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "faccount", false);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "npmnum", false);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "ffundset_else", false);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "fchoice", false);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "funits", false);
								getBillCardPanelWrapper().getBillCardPanel().getBillModel().setCellEditable(i, "vanjietype", false);
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "faccount");
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfscode1");
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "npmnum");
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "ffundset_else");
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "fchoice");
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vanjietype");
								getBillCardPanelWrapper().getBillCardPanel().setBodyValueAt(null, i, "vfsname");

							}
						}
					}

				}
			}
	}
	/**
	 * 点击修改按钮时,选中的是否款项字段时,设置关联的字段是否可以编辑
	 * 如果选中了是否款项,则可以编辑,否则不可以编辑
	 * */
	protected void onBoEdit() throws Exception
	{
		super.onBoEdit();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_situation").setEnabled(true);
		//设置方式编码是否可以编辑  如果系统生成不可编辑否则可以
		SuperVO headVO = (SuperVO)getBillUI().getVOFromUI().getParentVO();
		Object pk_proOBJ = headVO.getAttributeValue("pk_project");
		String  pk_project=null;
		if(pk_proOBJ != null)
			pk_project = pk_proOBJ.toString();
		Object vpmcodeOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").getValueObject();
		if(vpmcodeOBJ != null){
			String vpmcode = vpmcodeOBJ.toString();
			BillCodeObjValueVO codeObjVO = new BillCodeObjValueVO();
			codeObjVO.setAttributeValue(IDownList.billcode_corp
					, _getCorp().getPrimaryKey());
			codeObjVO.setAttributeValue(IDownList.billcode_targetassesspro
,pk_project);

			boolean flag =
				((IBillCodeBusi) NCLocator.getInstance().lookup(IBillCodeBusi.class.getName()))
				.bIsSystemConstruct(getUIController().getBillType(), _getCorp().getPrimaryKey()
						, vpmcode, codeObjVO);
			getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").setEnabled(!flag);
		}
		String pk_corp = ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
		if(pk_corp.equals("0001"))
		{
			((ClientUI)getBillUI()).afterProjectEdit();
		}
		editCard();

	}

	@Override
	protected void onBoElse(int intBtn) throws Exception {
		String pk_corp=ClientEnvironment.getInstance().getCorporation().getPrimaryKey();
		PSBDBusinessDelegator delegator=new PSBDBusinessDelegator();
		switch (intBtn) {
		case IPSButton.ImportSellCourse:
			BdPaymodeVO[] headvos=(BdPaymodeVO[])delegator.queryByCondition(BdPaymodeVO.class, "isnull(dr,0)=0 and pk_corp='0001'");
			if(headvos!=null &&headvos.length>0){
				SellCourseDlg sellcoursedialg = new SellCourseDlg(getBillUI(),headvos);
				if (sellcoursedialg.showModal() == UIDialog.ID_OK){
					AggregatedValueObject billVO = sellcoursedialg.getRetVo();
					if (((BillManageUI) getBillUI()).isListPanelSelected()) {
						((BillManageUI) getBillUI()).setCurrentPanel(BillTemplateWrapper.CARDPANEL);
						getBufferData().updateView();
					}
					if(billVO!=null){
						getBillUI().setBillOperate(IBillOperate.OP_ADD);
						//清除表头记录主键
						billVO.getParentVO().setPrimaryKey(null);
						//设置表头方式编码为空，允许用户输入或者自动生成
						((BdPaymodeVO)billVO.getParentVO()).setVpmcode(null);
						//设置表头公司主键，之前为集团的主键。
						((BdPaymodeVO)billVO.getParentVO()).setPk_corp(pk_corp);
						//清除表体记录主键
						clearChildPk(billVO.getChildrenVO());
						//设置表体公司主键，之前为集团的主键。
						setChildPk_corp(billVO.getChildrenVO(),pk_corp);
						// 设置界面数据
						getBillUI().setCardUIData(billVO);
						getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").setEnabled(true);
						editCard();

					}
				}else{
					return;
				}
		  } else{
			  MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPTH3011005-000002")//@res "提示"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-002858")//@res "无集团定义的销售进程！"
);
		  }
		}
	}



	private void clearChildPk(CircularlyAccessibleValueObject[] vos)
			throws Exception {
		if (vos == null || vos.length == 0)
			return;
		for (int i = 0; i < vos.length; i++) {
			vos[i].setPrimaryKey(null);
		}
	}

	private void setChildPk_corp(CircularlyAccessibleValueObject[] vos,String pk_corp) throws Exception {
		if (vos == null || vos.length == 0)
			return;
		for (int i = 0; i < vos.length; i++) {
			((BdPaymodeBVO)vos[i]).setPk_corp(pk_corp);
		}
	}

	protected void onBoLineDel() throws Exception
	{

		if(this.isBeforeCourse()==true)
		{
			MessageDialog.showErrorDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "错误"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000077")//@res "当前销售进程是其他进程的前置进程，不允许删除！"
);
		}
		else
		{

			super.onBoLineDel();
		}
		sortNum();
	}

	/**
	 *
	 * 创建者：李长松
	 * 方法说明：判断用户选中的进程是否是其他销售进程的前置进程
	 * @创建时间：2007-3-7 下午08:18:11
	 * 修改者：lics
	 * @修改时间：2007-3-7 下午08:18:11
	 * @return
	 *
	 */
	private boolean isBeforeCourse(){
		boolean returnValue=false;
		int rowID=getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		if(rowID<=0){
		//	MessageDialog.showErrorDlg(getBillUI(), "错误", "没有选中要删除进程！");
			returnValue=false;
		}
		else{
			String currCourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(rowID, "fpmcourse")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(rowID, "fpmcourse").toString();
			if(currCourse!=null && !currCourse.trim().equals(""))
			{
				for(int i=0;i<getBillCardPanelWrapper().getBillCardPanel().getRowCount();i++){
					Object tempObject=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fbfcourse")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fbfcourse").toString();
					if(tempObject!=null){
						String beforeCourse=tempObject.toString();
						if(beforeCourse.equals(currCourse)){
							return true;
						}
					}
				}
			}

		}
		return returnValue;
	}

	@Override
	protected String getHeadCondition() {
		return null;
	}
}