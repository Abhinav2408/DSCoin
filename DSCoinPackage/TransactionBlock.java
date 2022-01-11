package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  public TransactionBlock(Transaction[] t) {
	trarray = new Transaction[t.length];
	for(int i = 0; i < t.length ; i++)
	{
		trarray[i] = t[i];
	}
	this.previous = null;
	this.dgst = null;
	Tree = new MerkleTree();
	Tree.Build(t);
	trsummary = Tree.rootnode.val;
  }

  public boolean checkTransaction (Transaction t) {
	TransactionBlock tb = t.coinsrc_block;
	if(tb == null)
	{
		return true;
	}
	boolean x = false;
	for(int i = 0 ; i < tb.trarray.length ; i++)
	{
		if(tb.trarray[i].coinID.equals(t.coinID) && (tb.trarray[i].Destination == t.Source))
		{
			x = true;
			break;
		}
	}
	if(!x)
	{
		return false;
	}
	tb = this.previous;
	while(tb!=t.coinsrc_block)
	{
		for(int i = 0 ; i<tb.trarray.length; i++)
		{
			if(tb.trarray[i].coinID.equals(t.coinID))
			{
				return false;
			}
		}
		tb = tb.previous;
	}
	return true;
  }
}
