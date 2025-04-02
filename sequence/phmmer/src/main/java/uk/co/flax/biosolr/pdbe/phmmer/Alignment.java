package uk.co.flax.biosolr.pdbe.phmmer;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Alignment {
  
  public static double SIGNIFICANCE_THRESHOLD = 1.0d;
  
  private String target;
  
  private String species;
  
  private String description;
  
  private double score;

  private double eValue;

  private double eValueInd;

  private double eValueCond;

  private String querySequence;
  
  private int querySequenceStart;
  
  private int querySequenceEnd;
  
  private String match;
  
  private String targetSequence;
  
  private int targetSequenceStart;
  
  private int targetSequenceEnd;
  
  private int targetEnvelopeStart;
  
  private int targetEnvelopeEnd;
  
  private String posteriorProbability;
  
  private double bias;
  
  private double accuracy;
  
  private double bitScore;
  
  private double identityPercent;
  
  private int identityCount;
  
  private double similarityPercent;
  
  private int similarityCount;
  
  public Alignment(JsonObject hit) {
    JsonObject metadata = hit.getJsonObject("metadata");
    target = metadata.getString("accession");
    species = metadata.getString("species");
    description = metadata.getString("description");
    score = hit.getJsonNumber("score").doubleValue();
    bias = hit.getJsonNumber("bias").doubleValue();

    JsonValue eValueJSON = hit.get("evalue");

    if (eValueJSON.getValueType() == JsonValue.ValueType.NUMBER) {
      eValue = hit.getJsonNumber("evalue").doubleValue();
    } else if (eValueJSON.getValueType() == JsonValue.ValueType.STRING) {
      eValue = Double.parseDouble(hit.getString("evalue"));
    }

    JsonArray domains = hit.getJsonArray("domains");
    for (int i = 0; i < domains.size(); ++i) {
      JsonObject domain = domains.getJsonObject(i);
      
      // skip insignificant matches (by ind. eValue)
      JsonValue eValue = domain.get("ievalue");
      if (eValue.getValueType() == JsonValue.ValueType.NUMBER) {
        eValueInd = domain.getJsonNumber("ievalue").doubleValue();
      } else if (eValue.getValueType() == JsonValue.ValueType.STRING) {
        eValueInd = Double.parseDouble(domain.getString("ievalue"));
      }

      if (eValueInd >= SIGNIFICANCE_THRESHOLD) continue;

      JsonValue ceValue = domain.get("cevalue");

      if (ceValue.getValueType() == JsonValue.ValueType.NUMBER) {
        eValueCond = domain.getJsonNumber("cevalue").doubleValue();
      } else if (ceValue.getValueType() == JsonValue.ValueType.STRING) {
        eValueCond = Double.parseDouble(domain.getString("cevalue"));
      }

      JsonObject alignment = domain.getJsonObject("alignment_display");

      querySequence = alignment.getString("model");
      querySequenceStart = alignment.getInt("hmmfrom");
      querySequenceEnd = alignment.getInt("hmmto");
      
      match = alignment.getString("mline");
      
      targetSequence = alignment.getString("aseq");
      targetSequenceStart = alignment.getInt("sqfrom");
      targetSequenceEnd = alignment.getInt("sqto");
      
      targetEnvelopeStart = domain.getInt("ienv");
      targetEnvelopeEnd = domain.getInt("jenv");
      
      posteriorProbability = alignment.getString("ppline");
      
      accuracy = domain.getJsonNumber("oasc").doubleValue();
      bitScore = domain.getJsonNumber("bitscore").doubleValue();

      JsonArray identity = alignment.getJsonArray("identity");
      JsonArray similarity = alignment.getJsonArray("similarity");
      
      identityPercent = 100 * identity.getJsonNumber(0).doubleValue();
      identityCount = identity.getInt(1);
      
      similarityPercent = 100 * similarity.getJsonNumber(0).doubleValue();
      similarityCount = similarity.getInt(1);
      
      // we consider only the first significant match
      break;
    }
  }

  public double getSignificanceThreshold() {
    return SIGNIFICANCE_THRESHOLD;
  }

  public String getTarget() {
    return target;
  }
  
  public double getEValue() {
    return eValue;
  }

  public double getEValueInd() {
    return eValueInd;
  }

  public double getEValueCond() {
    return eValueCond;
  }
  
  public double getBitScore() {
    return bitScore;
  }
  
  public String getSpecies() {
    return species;
  }
  
  public String getDescription() {
    return description;
  }

  public double getScore() {
    return score;
  }

  public String getQuerySequence() {
    return querySequence;
  }

  public int getQuerySequenceStart() {
    return querySequenceStart;
  }

  public int getQuerySequenceEnd() {
    return querySequenceEnd;
  }

  public String getMatch() {
    return match;
  }

  public String getTargetSequence() {
    return targetSequence;
  }

  public int getTargetSequenceStart() {
    return targetSequenceStart;
  }

  public int getTargetSequenceEnd() {
    return targetSequenceEnd;
  }

  public int getTargetEnvelopeStart() {
    return targetEnvelopeStart;
  }

  public int getTargetEnvelopeEnd() {
    return targetEnvelopeEnd;
  }

  public String getPosteriorProbability() {
    return posteriorProbability;
  }

  public double getBias() {
    return bias;
  }

  public double getAccuracy() {
    return accuracy;
  }

  public double getIdentityPercent() {
    return identityPercent;
  }

  public int getIdentityCount() {
    return identityCount;
  }

  public double getSimilarityPercent() {
    return similarityPercent;
  }

  public int getSimilarityCount() {
    return similarityCount;
  }

}
