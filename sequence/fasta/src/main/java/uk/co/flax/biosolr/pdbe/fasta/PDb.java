package uk.co.flax.biosolr.pdbe.fasta;

public class PDb {
  
  public static class Id {
    public String id;
    
    public Id(String value) {
      this.id = value;
    }
    
    @Override
    public String toString() {
      return id;
    }
    
    @Override
    public boolean equals(Object that) {
      if (this == that) return true;
      if (! (that instanceof Id)) return false;
      return this.id.equals(((Id)that).id);
    }
    
    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }

  public static class Alignment {
    
    private StringBuilder querySeq = new StringBuilder();
    private StringBuilder retSeq = new StringBuilder();
  
    // set in constructor
    private double eValue;
    private Id pdbId;
    private String chain;
  
    // set by set() methods
    private Integer queryOverlapStart = null;
    private Integer queryOverlapEnd = null;
    private Integer dbOverlapStart = null;
    private Integer dbOverlapEnd = null;
    private Double percentIdentity = null;
  
    public Alignment(Id pdbId, String chain, double eValue) {
      this.pdbId = pdbId;
      this.chain = chain;
      this.eValue = eValue;
    }
  
    public boolean isComplete() {
      return percentIdentity != null;
    }
    
    public void addQuerySequence(String q) {
      querySeq.append(q);
    }
  
    public String getQuerySequenceString() {
      return querySeq.toString();
    }
  
    public void setQueryOverlapStart(int n) {
      queryOverlapStart = n;
    }
  
    public void setQueryOverlapEnd(int n) {
      queryOverlapEnd = n;
    }
    
    public void addReturnSequence(String r) {
      retSeq.append(r);
    }
  
    public String getReturnSequenceString() {
      return retSeq.toString();
    }
  
    public void setDbOverlapStart(int n) {
      dbOverlapStart = n;
    }
  
    public void setDbOverlapEnd(int n) {
      dbOverlapEnd = n;
    }
   
    public void setPercentIdentity(double d) {
      percentIdentity = d;
    }
  
    public double getPercentIdentity() {
      return percentIdentity;
    }
  
    public double getEValue() {
      return eValue;
    }
  
    public Id getPdbId() {
      return pdbId;
    }
  
    public String getChain() {
      return chain;
    }
  
    public int getQueryOverlapStart() {
      if (queryOverlapStart == null) {
        throw new RuntimeException("queryOverlapStart not set");
      }
      return queryOverlapStart;
    }
  
    public int getQueryOverlapEnd() {
      if (queryOverlapEnd == null) {
        throw new RuntimeException("queryOverlapEnd not set");
      }
      return queryOverlapEnd;
    }
  
    public int getDbOverlapStart() {
      if (dbOverlapStart == null) {
        throw new RuntimeException("dbOverlapStart not set");
      }
      return dbOverlapStart;
    }
  
    public int getDbOverlapEnd() {
      if (dbOverlapEnd == null) {
        throw new RuntimeException("dbOverlapEnd not set");
      }
      return dbOverlapEnd;
    }
    
    public String getPdbIdChain() {
      return String.format("%s_%s", pdbId.id, chain);
    }
  
    public String toString() {
      return String.format("[%s:%0.2f]", getPdbIdChain(), eValue);
    }
  }
  
}
