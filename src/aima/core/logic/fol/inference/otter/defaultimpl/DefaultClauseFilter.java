package aima.core.logic.fol.inference.otter.defaultimpl;

import aima.core.logic.fol.inference.otter.ClauseFilter;
import aima.core.logic.fol.kb.data.Clause;

import java.util.Set;

/**
 * @author Ciaran O'Reilly
 * 
 */
public class DefaultClauseFilter implements ClauseFilter {
	public DefaultClauseFilter() {

	}

	//
	// START-ClauseFilter
	public Set<Clause> filter(Set<Clause> clauses) {
		return clauses;
	}

	// END-ClauseFilter
	//
}
