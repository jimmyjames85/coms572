package aima.core.logic.fol.inference.otter;

import aima.core.logic.fol.kb.data.Clause;

import java.util.Set;

/**
 * @author Ciaran O'Reilly
 * 
 */
public interface ClauseFilter {
	Set<Clause> filter(Set<Clause> clauses);
}
