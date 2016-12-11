+Integer {
	midistr {
		arg favorDirection = 0;
		var str = switch(this%12)
		{0} {"C"}
		{1} {case
			{favorDirection >= 0}
			{"C#"}
			{"Db"}
		}
		{2} {"D"}
		{3} {case
			{favorDirection <= 0}
			{"Eb"}
			{"D#"}
		}
		{4} {"E"}
		{5} {"F"}
		{6} {case
			{favorDirection >= 0}
			{"F#"}
			{"Gb"}
		}
		{7} {"G"}
		{8} {case
			{favorDirection <= 0}
			{"Ab"}
			{"G#"}
		}
		{9} {"A"}
		{10} {case
			{favorDirection <= 0}
			{"Bb"}
			{"A#"}
		}
		{11} {"B"}
		{^"??"};
		str = str ++ ((this/12).floor-1);
		^str;
	}

	cpsstr {
		^this.cpsmidi.midistr;
	}

	round24tet {
		^this.abs.cpsmidi.round(0.5).midicps;
	}

	round12tet {
		^this.abs.cpsmidi.round.midicps;
	}

	roundNtet {
		arg ntet, miditonic=60;
		var offset = miditonic%12;
		^((this.abs.cpsmidi-offset).round(12/ntet)+offset).midicps;
	}
}