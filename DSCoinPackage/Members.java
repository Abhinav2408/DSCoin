package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;
  public boolean sorted;
  public Members(){
	sorted = false;
  }
  public void sort(){
	for(int i = 0; i < mycoins.size(); i++)
	{
		for(int j = i+1 ; j< mycoins.size() ; j++)
		{
			Pair<String, TransactionBlock> temp = null;
			if(Integer.parseInt(mycoins.get(i).first) > Integer.parseInt(mycoins.get(j).first))
			{
				temp = mycoins.get(j);
				mycoins.set(j , mycoins.get(i));
				mycoins.set(i , temp);
			}
		}
	}
	sorted = true;
  }
  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {

	if(!sorted)
	{
		this.sort();
	}
	Pair<String, TransactionBlock> x = mycoins.get(0);
	mycoins.remove(0);
	Transaction tobj = new Transaction();
	tobj.coinID = x.first;
	tobj.Source = this;
	tobj.coinsrc_block = x.second;
	for(int i = 0 ; i < DSobj.memberlist.length ; i++)
	{
		if(DSobj.memberlist[i].UID.equals(destUID))
		{
			tobj.Destination = DSobj.memberlist[i];
			break;
		}
	}
	int i = 0;
	while(in_process_trans[i]!=null)
	{
		i++;
	}
	in_process_trans[i] = tobj;
	DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
	if(!sorted)
	{
		this.sort();
	}
	Pair<String, TransactionBlock> x = mycoins.get(0);
	mycoins.remove(0);
	Transaction tobj = new Transaction();
	tobj.coinID = x.first;
	tobj.Source = this;
	tobj.coinsrc_block = x.second;
	for(int i = 0 ; i < DSobj.memberlist.length ; i++)
	{
		if(DSobj.memberlist[i].UID.equals(destUID))
		{
			tobj.Destination = DSobj.memberlist[i];
			break;
		}
	}
	int i = 0;
	while(in_process_trans[i]!=null)
	{
		i++;
	}
	in_process_trans[i] = tobj;
	DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSobj) throws MissingTransactionException {
	TransactionBlock tb = DSobj.bChain.lastBlock;
	boolean found = false;
	int idx = 0;
	while(tb!=null)
	{
		for(int i = 0; i < tb.trarray.length; i++)
		{
			if(tb.trarray[i] == tobj)
			{
				found = true;
				idx = i;
				break;
			}
		}
		if(found)
		{
			break;
		}
		tb  = tb.previous;
	}
	if(!found)
	{
		throw new MissingTransactionException();
	}

	int end = tb.trarray.length - 1;
       	int start = 0;
       	TreeNode tempnode = tb.Tree.rootnode;

        while(tempnode.right != null && tempnode.left != null)
        {
        	if (idx > (start + end) / 2)
            	{
               		tempnode = tempnode.right;
                	start = (start + end) / 2 + 1;
            	}
            	else
            	{
               		tempnode = tempnode.left;
              		end = (start + end) / 2;
            	}
        }

        List<Pair<String, String>> l = new ArrayList<Pair<String,String >>();
       	while(tempnode.parent!=null)
        {
            	if(tempnode.parent.left == tempnode)
            	{
                	Pair<String,String> p = new Pair<String,String>(tempnode.val , tempnode.parent.right.val);
                	l.add(p);
            	}
            	else
            	{
                	Pair<String,String> p = new Pair<String,String>(tempnode.parent.left.val, tempnode.val);
                	l.add(p);
            	}
            	tempnode = tempnode.parent;
        }

        l.add(new Pair<String,String>(tempnode.val, null));
	List<Pair<String, String>> m = new ArrayList<Pair<String,String >>();
	if(tb.previous!=null)
	{
		Pair<String,String> p = new Pair<String,String>(tb.previous.dgst , null);
		m.add(p);
	}
	else
	{
		Pair<String,String> p = new Pair<String,String>(DSobj.bChain.start_string , null);
		m.add(p);
	}
	TransactionBlock temp = DSobj.bChain.lastBlock;
	while(temp!=tb.previous)
	{
		Pair<String,String> p = new Pair<String,String>(temp.dgst , temp.previous.dgst + "#" + temp.trsummary + "#" + temp.nonce);
		m.add(1,p);
		temp = temp.previous;
	}
	Pair<List<Pair<String,String>>, List<Pair<String,String>>> x = new Pair<List<Pair<String,String>>, List<Pair<String,String>>>(l,m);
	int i = 0;
	while(in_process_trans[i]!=tobj)
	{
		i++;
	}
	while(in_process_trans[i]!=null)
	{
		in_process_trans[i]=in_process_trans[i+1];
		i++;
	}
	Pair<String, TransactionBlock> cointb = new Pair<String, TransactionBlock>(tobj.coinID , tb);
	tobj.Destination.mycoins.add(cointb);
	tobj.Destination.sort();
	return x;
  }

  public void MineCoin(DSCoin_Honest DSobj) {
	Transaction[] t = new Transaction[DSobj.bChain.tr_count];
	int i = 0;
	while(i<t.length - 1)
	{
		Transaction x = DSobj.pendingTransactions.l.get(0);
		TransactionBlock src = x.coinsrc_block;
		boolean flag = false;
		for(int k = 0; k < src.trarray.length ; k++)
		{
			if(src.trarray[k].coinID.equals(x.coinID) && src.trarray[k].Destination == x.Source)
			{
				flag = true ;
				break;
			}
		}
		if(!flag)
		{
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
			continue;
		}
		TransactionBlock tt = DSobj.bChain.lastBlock;
		while(tt != src)
		{
			for(int a = 0; a < tt.trarray.length ; a++)
			{
				if(tt.trarray[a].coinID.equals(x.coinID))
				{
					flag  = false;
					break;
				}
			}
			if(!flag)
			{
				break;
			}
			tt = tt.previous;
		}
		if(!flag)
		{
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
			continue;
		}
		for(int k = 0 ; k<i ; k++)
		{
			if(t[k].coinID.equals(x.coinID))
			{
				try
				{
					DSobj.pendingTransactions.RemoveTransaction();
				}
				catch(Exception e)
				{}
				flag = false;
				break;
			}
		}
		if(flag)
		{
			t[i] = x;
			i++;
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
		}
	}
	Transaction minerRewardTransaction = new Transaction();
	minerRewardTransaction.coinID = Integer.toString(Integer.parseInt(DSobj.latestCoinID) + 1);
	DSobj.latestCoinID = minerRewardTransaction.coinID;
	minerRewardTransaction.Source = null;
	minerRewardTransaction.coinsrc_block = null;
	minerRewardTransaction.Destination = this;
	t[i] = minerRewardTransaction;
	TransactionBlock tb = new TransactionBlock(t);
	DSobj.bChain.InsertBlock_Honest(tb);
	Pair<String, TransactionBlock> cointb = new Pair<String, TransactionBlock>(minerRewardTransaction.coinID , tb);
	this.mycoins.add(cointb);
  }  

  public void MineCoin(DSCoin_Malicious DSobj) {
	Transaction[] t = new Transaction[DSobj.bChain.tr_count];
	int i = 0;
	while(i<t.length - 1)
	{
		Transaction x = DSobj.pendingTransactions.l.get(0);
		TransactionBlock src = x.coinsrc_block;
		TransactionBlock tx = DSobj.bChain.FindLongestValidChain();
		boolean flag = false;
		while(tx!=null)
		{
			if(tx==src)
			{
				flag = true;
				break;
			}
			tx = tx.previous;
		}
		if(!flag)
		{
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
			continue;
		}
		flag = false;
		for(int k = 0; k < src.trarray.length ; k++)
		{
			if(src.trarray[k].coinID.equals(x.coinID) && src.trarray[k].Destination == x.Source)
			{
				flag = true ;
				break;
			}
		}
		if(!flag)
		{
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
			continue;
		}
		TransactionBlock tt = DSobj.bChain.FindLongestValidChain();
		while(tt != src)
		{
			for(int a = 0; a < tt.trarray.length ; a++)
			{
				if(tt.trarray[a].coinID.equals(x.coinID))
				{
					flag  = false;
					break;
				}
			}
			if(!flag)
			{
				break;
			}
			tt = tt.previous;
		}
		if(!flag)
		{
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
			continue;
		}
		for(int k = 0 ; k<i ; k++)
		{
			if(t[k].coinID.equals(x.coinID))
			{
				try
				{
					DSobj.pendingTransactions.RemoveTransaction();
				}
				catch(Exception e)
				{}
				flag = false;
				break;
			}
		}
		if(flag)
		{
			t[i] = x;
			i++;
			try
			{
				DSobj.pendingTransactions.RemoveTransaction();
			}
			catch(Exception e)
			{}
		}
	}
	Transaction minerRewardTransaction = new Transaction();
	minerRewardTransaction.coinID = Integer.toString(Integer.parseInt(DSobj.latestCoinID) + 1);
	DSobj.latestCoinID = minerRewardTransaction.coinID;
	minerRewardTransaction.Source = null;
	minerRewardTransaction.coinsrc_block = null;
	minerRewardTransaction.Destination = this;
	t[i] = minerRewardTransaction;
	TransactionBlock tb = new TransactionBlock(t);
	DSobj.bChain.InsertBlock_Malicious(tb);
	Pair<String, TransactionBlock> cointb = new Pair<String, TransactionBlock>(minerRewardTransaction.coinID , tb);
	this.mycoins.add(cointb);
  }  
}
