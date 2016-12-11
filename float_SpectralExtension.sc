+Float {
	midistr {
		arg favorDirection = 0;
		var str, cdflag = false;
		str = switch(this.round(0.5)%12.0)
		{0.0} {"C"}
		{0.5} {"C‡"}
		{1.0} {case
			{favorDirection >= 0}
			{"C#"}
			{"Db"}
		}
		{1.5} {"Dd"}
		{2.0} {"D"}
		{2.5} {"D‡"}
		{3.0} {case
			{favorDirection <= 0}
			{"Eb"}
			{"D#"}
		}
		{3.5} {"Ed"}
		{4.0} {"E"}
		{4.5} {case
			{favorDirection >= 0}
			{"E‡"}
			{"Fd"}
		}
		{5.0} {"F"}
		{5.5} {"F‡"}
		{6.0} {case
			{favorDirection >= 0}
			{"F#"}
			{"Gb"}
		}
		{6.5} {"Gd"}
		{7.0} {"G"}
		{7.5} {"G‡"}
		{8.0} {case
			{favorDirection <= 0}
			{"Ab"}
			{"G#"}
		}
		{8.5} {"Ad"}
		{9.0} {"A"}
		{9.5} {"A‡"}
		{10.0} {case
			{favorDirection <= 0}
			{"Bb"}
			{"A#"}
		}
		{10.5} {"Bd"}
		{11.0} {"B"}
		{11.5} {case
			{favorDirection >= 0}
			{"B‡"}
			{cdflag = true; "Cd"}
		}
		{^"??"};
		str = str ++ ((this.round(0.5)/12.0).floor-cdflag.if {0} {1});
		^str;
	}

	cpsstr {
		arg favorDirection=0;
		^this.cpsmidi.midistr(favorDirection);
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