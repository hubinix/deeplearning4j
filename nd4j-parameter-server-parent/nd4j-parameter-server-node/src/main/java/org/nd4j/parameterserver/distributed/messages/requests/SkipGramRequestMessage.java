package org.nd4j.parameterserver.distributed.messages.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.nd4j.parameterserver.distributed.logic.WordVectorStorage;
import org.nd4j.parameterserver.distributed.messages.BaseVoidMessage;
import org.nd4j.parameterserver.distributed.messages.TrainingMessage;
import org.nd4j.parameterserver.distributed.messages.VoidMessage;
import org.nd4j.parameterserver.distributed.messages.intercom.DistributedDotMessage;
import org.nd4j.parameterserver.distributed.messages.intercom.DistributedSkipGramMessage;
import org.nd4j.parameterserver.distributed.training.TrainerProvider;
import org.nd4j.parameterserver.distributed.training.TrainingDriver;

/**
 * This is batch message, describing simple SkipGram round
 *
 * We assume this message is created on Client, and passed to selected Shard
 * Shard which received this message becomes a driver, which handles processing
 *
 * @author raver119@gmail.com
 */
@Data
public class SkipGramRequestMessage extends BaseVoidMessage implements TrainingMessage {

    // learning rate for this sequence
    protected double alpha;

    // current word & lastWord
    protected int w1;
    protected int w2;

    // following fields are for hierarchic softmax
    // points & codes for current word
    protected int[] points;
    protected byte[] codes;

    protected short negSamples;

    protected long nextRandom;

    protected SkipGramRequestMessage() {
        super(0);
    }

    public SkipGramRequestMessage(int w1, int w2, int[] points, byte[] codes, short negSamples, double lr, long nextRandom) {
        this();
        this.w1 = w1;
        this.w2 = w2;
        this.points = points;
        this.codes = codes;
        this.negSamples = negSamples;
        this.alpha = lr;
        this.nextRandom = nextRandom;
    }

    /**
     * This method does actual training for SkipGram algorithm
     */
    @Override
    @SuppressWarnings("unchecked")
    public void processMessage() {
        /**
         * This method in reality just delegates training to specific TrainingDriver, based on message type.
         * In this case - SkipGram training
         */
        // FIXME: we might use something better then unchecked type cast here
        TrainingDriver<SkipGramRequestMessage> sgt = (TrainingDriver<SkipGramRequestMessage>) trainer;
        sgt.startTraining(this);
    }

    @Override
    public boolean isJoinSupported() {
        return true;
    }

    @Override
    public void joinMessage(VoidMessage message) {
        // TODO: apply proper join handling here
        alpha += ((SkipGramRequestMessage) message).getAlpha();
    }
}
