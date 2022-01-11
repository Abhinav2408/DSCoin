package DSCoinPackage;
import HelperClasses.*;
public class Moderator
 {
  Members mod;
  public Moderator(){
	mod = new Members();
	mod.UID = "Moderator";
  }
  public void initializeDSCoin(DSCoin_Honest DSobj, int coinCount) {
	int j =DSobj.bChain.tr_count - 1;
	int i =0;
	Transaction[] x = new Transaction[coinCount];
	for(int k = 0; k < coinCount; k++)
	{
		Transaction t = new Transaction();
		t.coinID = Integer.toString(100000 + k);
		t.Source = this.mod;
		t.Destination = DSobj.memberlist[k%DSobj.memberlist.length];
		t.coinsrc_block = null;
		x[k] = t;
	}
	DSobj.latestCoinID = Integer.toString(100000 + coinCount - 1);
	while(j<coinCount)
	{
		Transaction[] y = new Transaction[DSobj.bChain.tr_count];
		for(int k = i; k <=j; k++)
		{
			y[k-i] = x[k];
		}
		i = j+1;
		j = i + DSobj.bChain.tr_count - 1;
		TransactionBlock tb = new TransactionBlock(y);
		for(int k = 0; k < y.length ; k++)
		{
			Pair<String, TransactionBlock> cointb = new Pair<String, TransactionBlock>(y[k].coinID , tb);
			y[k].Destination.mycoins.add(cointb);
		}
		DSobj.bChain.InsertBlock_Honest(tb);
	}
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSobj, int coinCount) {
	int j =DSobj.bChain.tr_count - 1;
	int i =0;
	Transaction[] x = new Transaction[coinCount];
	for(int k = 0; k < coinCount; k++)
	{
		Transaction t = new Transaction();
		t.coinID = Integer.toString(100000 + k);
		t.Source = this.mod;
		t.Destination = DSobj.memberlist[k%DSobj.memberlist.length];
		t.coinsrc_block = null;
		x[k] = t;
	}
	DSobj.latestCoinID = Integer.toString(100000 + coinCount - 1);
	while(j<coinCount)
	{
		Transaction[] y = new Transaction[DSobj.bChain.tr_count];
		for(int k = i; k <=j; k++)
		{
			y[k-i] = x[k];
		}
		i = j+1;
		j = i + DSobj.bChain.tr_count - 1;
		TransactionBlock tb = new TransactionBlock(y);
		for(int k = 0; k < tb.trarray.length ; k++)
		{
			Pair<String, TransactionBlock> cointb = new Pair<String, TransactionBlock>(tb.trarray[k].coinID , tb);
			tb.trarray[k].Destination.mycoins.add(cointb);
		}
		DSobj.bChain.InsertBlock_Malicious(tb);
	}
  }
}
