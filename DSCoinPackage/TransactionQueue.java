package DSCoinPackage;
import java.util.*;
public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;
  public List<Transaction> l = new ArrayList<Transaction>();
  public void AddTransactions (Transaction transaction) {
	if(numTransactions == 0)
	{
		firstTransaction = transaction;
	}
	l.add(transaction);
	lastTransaction = transaction;
	numTransactions++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
	if(numTransactions == 0)
	{
		throw new EmptyQueueException();
	}
	else
	{
		Transaction t = l.get(0);
		l.remove(0);
		numTransactions--;
		return t;
	}
  }

  public int size() {
    return this.numTransactions;
  }
}
