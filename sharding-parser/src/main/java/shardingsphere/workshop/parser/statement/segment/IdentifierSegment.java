
package shardingsphere.workshop.parser.statement.segment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.ASTNode;

import java.util.Objects;

/**
 * Identifier segment.
 */
@RequiredArgsConstructor
@Getter
public final class IdentifierSegment implements ASTNode {
    
    private final String value;

    @Override
    public boolean equals(Object obj) {
        if(Objects.isNull(obj)){
            return false;
        }
        IdentifierSegment identifierSegment = (IdentifierSegment)obj;
        return Objects.equals(value,identifierSegment.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
