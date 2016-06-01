package ie;

import org.ansj.domain.*;
import org.ansj.domain.Term;

/**
 * Created by lfc on 2016/5/17.
 */
public class MyTerm {

    private Term term;

    public MyTerm(Term t) {
        this.term = t;
    }


    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj.getClass().equals(MyTerm.class)) {
            MyTerm t = (MyTerm) obj;
            String name = t.getTerm().getName();
            String pos = t.getTerm().natrue().natureStr;
            isEqual = (name.equals(this.term.getName()) && (this.term.getNatureStr()).equals(pos));
        } else {
            isEqual = false;
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
