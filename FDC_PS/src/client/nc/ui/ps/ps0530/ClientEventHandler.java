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
		// TODO �Զ����ɹ��캯�����!
	}
	@SuppressWarnings("unchecked")
	protected void save()throws Exception	{
		int count=getBillCardPanelWrapper().getBillCardPanel().getBillTable().getRowCount();
		Object fpmtypeOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("fpmtype").getValueObject()==null?"0":getBillCardPanelWrapper().getBillCardPanel().getHeadItem("fpmtype").getValueObject();
		String fpmtype = fpmtypeOBJ.toString();
		try {
			BdPaymodeBVO[] payModel=(BdPaymodeBVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(BdPaymodeBVO.class.getName());
			int unitnum=0;//��¼���뷽ʽΪ������β�����Ŀ���������1�����򱨴�
			if(payModel !=null &&payModel.length>=1){
				for(BdPaymodeBVO tempvo:payModel){
					if(tempvo!=null&&tempvo.getFchoice()!=null && tempvo.getFchoice().intValue()==3){
						unitnum++;
					}
				}

				if(unitnum>1){
					MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002954")//@res "ֻ����һ��������̵����뷽ʽΪ������β�"

);
					return ;
				}
				if(unitnum==0){
					if("2".equals(fpmtype)){ //����������޿����������У�飡
						//do nothing
					}else{
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
								, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002955")//@res "��������У�������һ�����̵����뷽ʽ����Ϊ������β�"

						);
						return ;
					}
				}
			}
			for(int i=0;i<count;i++)
			{
				Integer intfpmcourse = payModel[i].getFpmcourse();  //��������
				Integer intffunction = payModel[i].getFfunction();	//���ܵ�
				if(i ==0){

					/**��һ�е�����!*/
					Object objSpacing = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ispacing");//���ڼ��
					Object objFinish = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "dfinishdate");//�̶�����
//					String fpmcourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fpmcourse")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fpmcourse").toString();//��������
//					String ffunction=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ffunction")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ffunction").toString();//���ܵ�
					String fbfcourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fbfcourse")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fbfcourse").toString();//ǰ�ý���
					if(intfpmcourse==null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000050")//@res "�������̲���Ϊ��"
);
						return ;
					}
					//�Ϲ� //ǩԼ
					if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SUBSCRIBE){
						if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SIGNING){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000052")//@res "��һ�еĹ������̱������Ϲ�����ǩԼ"
);
							return ;
						}
					}

					/**���ܵ� */
					if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SUBSCRIBE){
						if(intfpmcourse==null||intfpmcourse!=IPsDataMapping.SELL_COURSE_SIGNING){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000056")//@res "��һ�еĹ��ܵ�������Ϲ�����ǩԼ"
);
							return ;
						}
					}
					if(fbfcourse !=null && fbfcourse.length()>0){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000057")//@res "��һ��û��ǰ�ý���"
);
						return ;
					}

					if(intffunction==null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000051")//@res "���ܵ㲻��Ϊ��"
);
						return ;
					}

					if(objSpacing !=null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000053")//@res "��һ�в�Ӧ����д���ڼ��"
);
						return ;
					}
					if(objFinish !=null){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000054")//@res "��һ�в�Ӧ����д�̶�����"
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
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000055")//@res "���ʽ�������һ��'ǩԼ'����"
);
							return ;
						}
					}

					String bisfunds=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund").toString();
					if(bisfunds !=null && !bisfunds.equals(""))
					{
						if(new UFBoolean(bisfunds).booleanValue()){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000058")//@res "��һ�й�������Ϊ�Ϲ�������ǩԼʱ����ѡ���Ƿ����,��ȡ���Ƿ����"
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
					Object objSpacing = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ispacing");//���ڼ��
					Object objFinish = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "dfinishdate");//�̶�����
//					Object objFpmcourse=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "fpmcourse");//��������,ȡֵΪ���֣��󶨽��
//					Object ffunction=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "ffunction");//���ܵ�
//					Object vfscode1OBJ=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "vfscode1");//����
					Object fundset = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "pk_fundset_fund");
					UIRefPane vfscode1OBJ=(UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getBodyItem("vfscode1").getComponent();//����
					String bisfund=getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund")==null?null:getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund").toString();
					Boolean fund=Boolean.parseBoolean(bisfund);
					int row=i+1;
					if(intffunction ==null ){
						if(fund==false){
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000060")//@res "��,���ܵ���������д����һ��"
);
							return ;
						}
					}
					if(intfpmcourse!=null &&(intfpmcourse==IPsDataMapping.SELL_COURSE_LOAN_UP || intfpmcourse==IPsDataMapping.SELL_COURSE_GJJLOAN_UP)){
						vfscode1OBJ.setPK(fundset);
						if(vfscode1OBJ!=null&&(!vfscode1OBJ.getRefCode().equals("0104")&&!vfscode1OBJ.getRefCode().equals("0202")))  //���ҿ��λ���ҿ�
						{
//							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
//									, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002805")//@res "��ҵ�������𣩰��ҵ��˽��̵Ŀ���ӦΪ���ҿ�"
//									);
//							return ;
						}

					}

					//�����һ�й������̵����ݲ���ǩԼ,��ô�Ժ���б�����ǩԼ����
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
									MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000061")//@res "���ܵ��ظ�"
);
									return ;
								}else{
									ffundMap.put(tempvo.getFfunction(), tempvo.getFfunction());
								}
							}
							//����ѡ��Ľ��̺Ŵ���fpmcoursemap�У��Ա㰴�������Ͱ��ҵ��˵�Լ����������
							if(tempvo!=null&&tempvo.getFpmcourse()!=null&&tempvo.getFpmcourse().intValue()>0){
								fpmcoursemap.put(tempvo.getFpmcourse(),tempvo.getFpmcourse());
							}
						}
					}
					//�������ѡ����ҵ���������������ѡ����ҵ���ҵ��ˣ���֮����ͬ�����ɶԳ��֣�ͬ�������𰴽������͹�������
					//��4��ѡ���У���ҵ����������Ӧ����ֵ��С��Ϊ14;��ҵ���ҵ���=16;�����𰴽�����=22;�����𰴽ҵ���=23.
					if( ( fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_PROCEDURE)&&!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_UP))
						  ||(!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_PROCEDURE)&&fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_LOAN_UP))
					  )
					{
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
								, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002771")//@res "������Ӧͬʱ������ҵ������������ҵ���ҵ���"
						);
						return ;
					}

					if( ( fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_PROCEDURE)&&!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_UP))
							  ||(!fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_PROCEDURE)&&fpmcoursemap.containsKey(IPsDataMapping.SELL_COURSE_GJJLOAN_UP))
						  )
						{
							MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
									, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002772")//@res "������Ӧͬʱ���������𰴽������͹����𰴽ҵ���"
							);
							return ;
						}

					if(hasSign==false){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000055")//@res "���ʽ�������һ��'ǩԼ'����"
);
						return ;
					}
					if(hasfund==true){
						MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000062")//@res "���ѡ�����Ƿ����,��Ӧ�����������(���㷽ʽ,����,��ֵ,����������,���뷽ʽ)"!
);
						return ;
					}


					if(intfpmcourse!=null &&( intfpmcourse==IPsDataMapping.SELL_COURSE_SUBSCRIBE || intfpmcourse==IPsDataMapping.SELL_COURSE_SIGNING)){

						if(fund!=null)
						{
							if(fund){
								MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000063")//@res "����������Ϊ�Ϲ�������ǩԼʱ����ѡ���Ƿ����,��ȡ���Ƿ����"
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
							/**ǰ�ý��� */
							if(i >=1){
								if((objSpacing==null || objSpacing.toString().trim().equals("")) && (objFinish==null || objFinish.toString().trim().equals(""))){
									MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000064")//@res "�е����ڼ�����߹̶����ڱ�����д����һ��"
);
									return ;
								}
								if((objSpacing!=null)&&(objFinish!=null)){
									MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
,NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000065")//@res "�е����ڼ�����߹̶�����ֻ����д����һ��"
);
									return ;
								}
							}

							if(intffunction!=null) {
								MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000066")//@res "�Ƿ������Ϊ(��)���򱾴β���û�ж�Ӧ���ܵ�"
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
						MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000064")//@res "�е����ڼ�����߹̶����ڱ�����д����һ��"
);
						return ;
					}
					if((objSpacing!=null)&&(objFinish!=null))
					{
						MessageDialog.showErrorDlg(this.getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
,NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+row+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000065")//@res "�е����ڼ�����߹̶�����ֻ����д����һ��"
);
						return ;

					}

				}


			}

		}catch(Exception e)	{
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		//���ݱ�ͷ��ҵ̬�жϱ���Ŀ���
		if(bisposition().booleanValue()==false){
			return ;
		}

//		//�����ʽ����Ϊ�� ���Զ����ɱ��� EDIT guanyj
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
	 * @���� ��ʤ��
	 * @����ʱ�䣺2007-9-20 ����09:01:52
	 * @˵�����жϱ�ͷ��ҵ̬������ǳ�λ����ô�����������п���Ҳ���붼�ǳ�λ�������ǳ�λ����붼�Ƿ���
	 */
	private Boolean bisposition() throws Exception{
		BdPaymodeVO paymodeVOObj=(BdPaymodeVO) getBillUI().getVOFromUI().getParentVO();
		if(paymodeVOObj.getPk_situation() ==null||paymodeVOObj.getPk_situation().trim().length()==0){
			return true;
		}
		HashMap postMap=bispostMap();//�϶��ǲ��ǳ�λ��HASHMAP,���ҵ̬�����Ͷ�Ӧ��ҵ̬VO
		HashMap fundCodeMap=getFoundType();//�жϿ������͵�CODE
		BdOperstateVO stateObj=(BdOperstateVO) postMap.get(paymodeVOObj.getPk_situation());
		BdPaymodeBVO[] paymodeBVOs=(BdPaymodeBVO[])getBillCardPanelWrapper().getBillCardPanel().getBillModel().getBodyValueVOs(BdPaymodeBVO.class.getName());

		//�����ҵ̬�ǳ�λ�Ĵ������
		if(stateObj.getBisposition() !=null &&(stateObj.getBisposition().booleanValue()==true|| stateObj.getBisposition().equals("Y"))){
			for(int i=0;i<paymodeBVOs.length;i++){
				UFBoolean fundBool=paymodeBVOs[i].getBisfund();
				if(fundBool !=null &&(fundBool.booleanValue()==true)){

					String curFoundType=fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund())==null?null:fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund()).toString();//�������͵ı���
					if(curFoundType !=null && !curFoundType.equals(IPsDataMapping.FUND_TYPE_CAR_MONEY)){
						int n=i+1;
						MessageDialog.showErrorDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+n+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000067")//@res "�еĿ���ǳ�λ��"
);
						return false;
					}
				}
			}
		}else{
			//ҵ̬���ǳ�λ�������Ŀ�����붼�Ƿ���
			for(int i=0;i<paymodeBVOs.length;i++){
				UFBoolean fundBool=paymodeBVOs[i].getBisfund();
				if(fundBool !=null &&(fundBool.booleanValue()==true)){

					String curFoundType=fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund())==null?null:fundCodeMap.get(paymodeBVOs[i].getPk_fundset_fund()).toString();//�������͵ı���
					if(curFoundType !=null && curFoundType.equals(IPsDataMapping.FUND_TYPE_CAR_MONEY)){
						int n=i+1;
						MessageDialog.showErrorDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000059")//@res "��"
+n+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000068")//@res "�еĿ���Ƿ���"
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
	 * @���� ��ʤ��
	 * @����ʱ�䣺2007-9-20 ����10:15:11
	 * @˵��������HashMap
	 * 		  PK: ��������
	 * 		  ֵ��:�������͵ı���CODE
	 */
	@SuppressWarnings("unchecked")
	private HashMap getFoundType() {
		BdFundtypeVO fundTypeVO = null;
		String returnStr = null;
		HashMap<String,String> fundTypeMap=new HashMap<String, String>();
		try {
			//��ѯ��������.
			BdFundsetVO[] fundsetVOs=(BdFundsetVO[])HYPubBO_Client.queryByCondition(BdFundsetVO.class, " isnull(dr,0)=0 ");
            //��ѯ�������ö�Ӧ�Ŀ������͡�
			for(BdFundsetVO setVOObj:fundsetVOs){
				fundTypeVO = (BdFundtypeVO) HYPubBO_Client.queryByPrimaryKey(BdFundtypeVO.class, setVOObj.getPk_fundtype());
				returnStr = fundTypeVO.getVftcode();
				fundTypeMap.put(setVOObj.getPrimaryKey(), returnStr);
			}

		} catch (UifException e) {
			// TODO �Զ����� catch ��
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return fundTypeMap;
	}

	private HashMap bispostMap()throws Exception{//���ҵ̬�����Ͷ�Ӧ��ҵ̬VO
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

		//��ӱ����ܺ͵���֤  edit by guanyj   ������������ɿ����������100% ���ݲ��޸�20080329 14:33
//		int rowCount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
//		UFDouble sum = new UFDouble(0);
//		for(int i=0;i<rowCount;i++){
//			Object bisfundOBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund");
//			if (bisfundOBJ != null) {
//				String bisfund = bisfundOBJ.toString();
//				if ((new UFBoolean(bisfund)).booleanValue() && "����(%)".equals(getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "faccount").toString())) {
//					sum = sum.add(new UFDouble(getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "npmnum").toString()));
//				}
//			}
//		}
//		if(sum.doubleValue() > 100)
//			throw new BusinessException("��������ܺʹ���100%�����������ֵ��");
		//end by guanyj
		//��֤������ø��ʽѡ���˰��ң����������������ް��������򱨴�ͬ�������ѡ���˷ǰ��ң���������в��ܳ��ְ�������guanyj
		Object fpmtypeOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("fpmtype").getValueObject();
		Object situationOBJ = getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_situation").getValueObject();
		boolean iscarsituation=false;
		if(situationOBJ!=null&&situationOBJ.equals("0001Y51000000004ZRGL"))  //ҵ̬�Ƿ�Ϊ��λ
		{
			iscarsituation=true;
		}else{
			iscarsituation=false;
		}

		if(fpmtypeOBJ == null){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000069")//@res "��ͷ ��ʽ����Ϊ�գ�"
);
		}
		String fpmtype = fpmtypeOBJ.toString();
		int rowCount = getBillCardPanelWrapper().getBillCardPanel().getRowCount();
		boolean isHave=false;	//�Ƿ��а��ҿ���
		boolean hasfund=false;	//�Ƿ��п���
		boolean ischeweikuan=false; //�Ƿ��г�λ��
		boolean iscaranjie=false;//�Ƿ��г�λ���ҿ�
		int j=0,k=0,n=0;//j��¼�������Ŀ��k��¼��λ�����Ŀ,n��¼��λ���ҿ����Ŀ��
		for(int i=0;i<rowCount;i++){
			Object bisfundOBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "bisfund");
			if (bisfundOBJ != null) {
				String bisfund = bisfundOBJ.toString();
				if ((new UFBoolean(bisfund)).booleanValue()) {//�ǿ���
					hasfund = true;
					if(i==0)
					{
						throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002835")//@res "��һ������ֻ��Ϊ�Ϲ���ǩԼ������ѡ����"
						);
					}
					j++;
					Object fundset = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "pk_fundset_fund");
					UIRefPane vfscode1OBJ=(UIRefPane)getBillCardPanelWrapper().getBillCardPanel().getBodyItem("vfscode1").getComponent();//����
					if(fundset == null){
						throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000070")//@res "����� "
								+(i+1)+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000071")//@res " �У�ѡ��Ϊ��������Ϊ��"
						);
					}
					vfscode1OBJ.setPK(fundset);
					if(vfscode1OBJ.getRefCode().equals("0104")){    //���ҿ�
						isHave=true;
					}

					if(vfscode1OBJ.getRefCode().equals("0202")){	//��λ���ҿ�
						iscaranjie=true;
						n++;
					}

					if(vfscode1OBJ.getRefCode().equals("0201")){	//��λ��
						ischeweikuan=true;
						k++;
					}
				}
			}
		}
/*		if(fpmtype.equals("0") && !isHave){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000073")//@res "�������������в����ް�������"
);
		}*/
		int carsitunum=n+k;
		if(fpmtype.equals("0")){
			if(!iscarsituation && (!isHave&&!iscaranjie))  //ҵ̬���ǳ�λ�Ҳ��������ҿ�
			{
//				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000073")//@res "�������������в����ް�������"
//				);
			}
			if(iscarsituation && !iscaranjie) //ҵ̬Ϊ��λ�Ҳ�������λ���ҿ�
			{
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-002856")//@res "ҵ̬Ϊ��λ�İ��������Ӧ������λ���ҿ�"
//@res "ҵ̬Ϊ��λ�İ��������Ӧ������λ���ҿ"
				);
			}

		}
/*		if(fpmtype.equals("1") && isHave){
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000074")//@res "�ǰ������������в��ܳ��ְ�������"
);
		}*/
		if(fpmtype.equals("1")){
			if(!iscarsituation && (isHave||iscaranjie))  //ҵ̬���ǳ�λ�Ұ����˰��ҿ�
			{
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000074")//@res "�ǰ������������в��ܳ��ְ�������"
			   );
			}
			if(iscarsituation && iscaranjie) //ҵ̬Ϊ��λ�Ұ����˳�λ���ҿ�
			{
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-002857")//@res "ҵ̬Ϊ��λ�ķǰ������������в��ܳ��ֳ�λ���ҿ"
//@res "ҵ̬Ϊ��λ�ķǰ������������в��ܳ��ֳ�λ���ҿ"
				   );
			}
		}

		if (fpmtype.equals("2") && hasfund) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000075")//@res "����������в��ܳ��ֿ��"
);
		}
		if(!iscarsituation&&(ischeweikuan||iscaranjie)&&j!=carsitunum)//���̿����а�����λ���λ���ҿ�����п�����̵Ŀ�������Ϊ����Ϊ��λ��Ŀ����λ���λ���ҿ.
		{
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-002824")//@res "��������а�����λ������п�����̵Ŀ�������Ϊ��λ��"
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
		// TODO �Զ����ɷ������
/*		if (MessageDialog.showYesNoDlg(getBillUI(), "ɾ��", "�Ƿ�ȷ��ɾ��������?")
				!= UIDialog.ID_YES) {
				return;
			}*/
		super.onBoDelete();
	}
	public void onBoAdd(ButtonObject obj)throws Exception
	{
		super.onBoAdd(obj);
		((ClientUI)getBillUI()).afterProjectEdit();
		//����ʱ���÷�ʽ����ɱ༭
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
    		// �������ݵ�Buffer
    		addDataToBuffer(queryVos);
    		updateBuffer();
        }else{
        	super.onBoQuery();
        }
	}

	/* ���� Javadoc��
	 * @see nc.ui.trade.manage.ManageEventHandler#onBoCopy()
	 */
	@Override
	protected void onBoCopy() throws Exception {
		// TODO �Զ����ɷ������
		//��ս�������
		super.onBoCopy();
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_project", null);
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_situation", null);
		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("pk_rebate", null);
		//getBillCardPanelWrapper().getBillCardPanel().setHeadItem("fpmtype", null);
		//getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vpmcode", null);
		//getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vpmname", null);
//		getBillCardPanelWrapper().getBillCardPanel().setHeadItem("vpmcode", ((ClientUI)getBillUI()).getBillNo());
		//���÷�ʽ����ɱ༭ EDIT by gyj
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
		// TODO �Զ����ɷ������
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
						//	���ҿ����ѡ�񰴽�����
						Object vfscode1OBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(count, "vfscode1");
						if(vfscode1OBJ == null){
							throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000070")//@res "����� "
									+(count+1)+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000071")//@res " �У�ѡ��Ϊ��������Ϊ��"
							);
						}
						String vfscode1 = vfscode1OBJ.toString();
						if(vfscode1.equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000072")//@res "���ҿ�"
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
//								���ҿ����ѡ�񰴽�����
								Object vfscode1OBJ = getBillCardPanelWrapper().getBillCardPanel().getBodyValueAt(i, "vfscode1");
								if(vfscode1OBJ == null) {
									throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000070")//@res "����� "
											+(i+1)+NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000071")//@res " �У�ѡ��Ϊ��������Ϊ��"
									);
								}
								String vfscode1 = vfscode1OBJ.toString();
								if(vfscode1.equals(NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000072")//@res "���ҿ�"
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
	 * ����޸İ�ťʱ,ѡ�е��Ƿ�����ֶ�ʱ,���ù������ֶ��Ƿ���Ա༭
	 * ���ѡ�����Ƿ����,����Ա༭,���򲻿��Ա༭
	 * */
	protected void onBoEdit() throws Exception
	{
		super.onBoEdit();
		getBillCardPanelWrapper().getBillCardPanel().getHeadItem("pk_situation").setEnabled(true);
		//���÷�ʽ�����Ƿ���Ա༭  ���ϵͳ���ɲ��ɱ༭�������
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
						//�����ͷ��¼����
						billVO.getParentVO().setPrimaryKey(null);
						//���ñ�ͷ��ʽ����Ϊ�գ������û���������Զ�����
						((BdPaymodeVO)billVO.getParentVO()).setVpmcode(null);
						//���ñ�ͷ��˾������֮ǰΪ���ŵ�������
						((BdPaymodeVO)billVO.getParentVO()).setPk_corp(pk_corp);
						//��������¼����
						clearChildPk(billVO.getChildrenVO());
						//���ñ��幫˾������֮ǰΪ���ŵ�������
						setChildPk_corp(billVO.getChildrenVO(),pk_corp);
						// ���ý�������
						getBillUI().setCardUIData(billVO);
						getBillCardPanelWrapper().getBillCardPanel().getHeadItem("vpmcode").setEnabled(true);
						editCard();

					}
				}else{
					return;
				}
		  } else{
			  MessageDialog.showHintDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPTH3011005-000002")//@res "��ʾ"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("HYPS","UPPHYPS-002858")//@res "�޼��Ŷ�������۽��̣�"
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
			MessageDialog.showErrorDlg(getBillUI(), NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000008")//@res "����"
, NCLangRes4VoTransl.getNCLangRes().getStrByID("hyps","UPPhyps-000077")//@res "��ǰ���۽������������̵�ǰ�ý��̣�������ɾ����"
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
	 * �����ߣ����
	 * ����˵�����ж��û�ѡ�еĽ����Ƿ����������۽��̵�ǰ�ý���
	 * @����ʱ�䣺2007-3-7 ����08:18:11
	 * �޸��ߣ�lics
	 * @�޸�ʱ�䣺2007-3-7 ����08:18:11
	 * @return
	 *
	 */
	private boolean isBeforeCourse(){
		boolean returnValue=false;
		int rowID=getBillCardPanelWrapper().getBillCardPanel().getBillTable().getSelectedRow();
		if(rowID<=0){
		//	MessageDialog.showErrorDlg(getBillUI(), "����", "û��ѡ��Ҫɾ�����̣�");
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