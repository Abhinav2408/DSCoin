package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
	CRF obj = new CRF(64);
	newBlock.previous = lastBlock;
	String s = "1000000001";
	if(lastBlock!=null)
	{
		String x = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
		while(!x.substring(0,4).equals("0000"))
		{
			s = Integer.toString(Integer.parseInt(s) + 1);
			x = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
		}
		newBlock.nonce = s;
		newBlock.dgst = x;
	}
	else
	{
		String x = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
		while(!x.substring(0,4).equals("0000"))
		{
			s = Integer.toString(Integer.parseInt(s) + 1);
			x = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
		}
		newBlock.nonce = s;
		newBlock.dgst = x;
	}
	lastBlock = newBlock;
  }
}
