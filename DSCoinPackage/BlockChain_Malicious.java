package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
	if(!tB.dgst.substring(0,4).equals("0000"))
	{
		return false;
	}

	CRF obj = new CRF(64);
	String s = "";
	if(tB.previous==null)
	{
		s = obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce);
	}
	else
	{
		s = obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce);
	}

	if(!tB.dgst.equals(s))
	{
		return false;
	}
	MerkleTree t = new MerkleTree();
	t.Build(tB.trarray);
	if(!(tB.trsummary.equals(tB.Tree.rootnode.val) && tB.Tree.rootnode.val.equals(t.rootnode.val)))
	{
		return false;
	}
	for(int i =0; i<tB.trarray.length; i++)
	{
		if(!tB.checkTransaction(tB.trarray[i]))
		{
			return false;
		}
	}
	return true;
  }

  public TransactionBlock FindLongestValidChain () {
	int max = 0;
	TransactionBlock tmax = null;
	for(int i = 0; lastBlocksList[i]!=null ; i++)
	{
		TransactionBlock temp = lastBlocksList[i];
		int l = 0;
		TransactionBlock t = null;
		while(temp!=null)
		{
			if(!this.checkTransactionBlock(temp))
			{
				l = 0;
				t =temp.previous;
			}
			else
			{
				l++;
			}
			temp = temp.previous;
		}
		if(l>=max)
		{
			max = l;
			tmax = t;
		}
	}
	return tmax;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
	TransactionBlock lastBlock = this.FindLongestValidChain();
	CRF obj = new CRF(64);
	newBlock.previous = lastBlock;
	String s = "1000000001";
	String x = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
	while(!x.substring(0,4).equals("0000"))
	{
		s = Integer.toString(Integer.parseInt(s) + 1);
		x = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
	}
	newBlock.nonce = s;
	newBlock.dgst = x;
	boolean flag = false;
	int i = 0;
	for(i = 0; lastBlocksList[i]!=null ; i++)
	{
		if(lastBlock == lastBlocksList[i])
		{
			lastBlocksList[i] = newBlock;
			flag = true;
			break;
		}
	}
	if(!flag)
	{
		lastBlocksList[i] = newBlock;
	}
	newBlock.previous = lastBlock;
  }
}
