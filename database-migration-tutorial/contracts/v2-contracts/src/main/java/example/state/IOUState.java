package example.state;


import com.google.common.collect.ImmutableList;
import example.contract.IOUContract;
import example.schema.IOUSchemaV1;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * The state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 */
@BelongsToContract(IOUContract.class)
public class IOUState implements LinearState, QueryableState {
    private final Integer value;
    private final Party lender;
    private final Party borrower;
    private final UniqueIdentifier linearId;
    private final Integer constraint_type;

    /**
     * @param value the value of the IOU.
     * @param lender the party issuing the IOU.
     * @param borrower the party receiving and approving the IOU.
     */
    public IOUState(Integer value,
                    Party lender,
                    Party borrower,
                    UniqueIdentifier linearId,
                    @Nullable Integer constraint_type)
    {
        this.value = value;
        this.lender = lender;
        this.borrower = borrower;
        this.linearId = linearId;
        this.constraint_type = constraint_type;
    }

    public Integer getValue() { return value; }
    public Party getLender() { return lender; }
    public Party getBorrower() { return borrower; }

    public Integer getConstraint_type() {
        return constraint_type;
    }

    @Override public UniqueIdentifier getLinearId() { return linearId; }
    @Override public List<AbstractParty> getParticipants() {
        return Arrays.asList(lender, borrower);
    }

    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof IOUSchemaV1) {
            return new IOUSchemaV1.PersistentIOU(
                    this.lender.getName().toString(),
                    this.borrower.getName().toString(),
                    this.value,
                    this.linearId.getId(),
                    this.constraint_type);
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new IOUSchemaV1());
    }

    @Override
    public String toString() {
        return String.format("IOUState(value=%s, lender=%s, borrower=%s, linearId=%s)", value, lender, borrower, linearId);
    }
}