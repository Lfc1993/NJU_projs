package ie;

/** pattern class
 * Created by lfc on 2016/5/12.
 */
public class Pattern {

    private MyTerm subject;
    private MyTerm predicate;
    private MyTerm object;

    public Pattern(MyTerm subject, MyTerm predicate, MyTerm object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public MyTerm getSubject() {
        return subject;
    }

    public void setSubject(MyTerm subject) {
        this.subject = subject;
    }

    public MyTerm getPredicate() {
        return predicate;
    }

    public void setPredicate(MyTerm predicate) {
        this.predicate = predicate;
    }

    public MyTerm getObject() {
        return object;
    }

    public void setObject(MyTerm object) {
        this.object = object;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(Pattern.class)) {
            Pattern p = (Pattern) obj;
            return (this.subject.equals(p.getSubject())) && (this.predicate.equals(p.getPredicate())) && (this.getObject().equals(p.getObject()));
        } else {
            return false;
        }
    }
}
