+Float {
	midistr48 {
		arg favorDirection = 0;
		var str, cdflag = false;
		str = switch(this.round(0.25)%12.0)
		{0.0} {"C"}
		{0.25} {"C↑"}
		{0.5} {"C‡"}
		{0.75} {case
			{favorDirection >= 0}
			{"C#↓"}
			{"Db↓"}
		}
		{1.0} {case
			{favorDirection >= 0}
			{"C#"}
			{"Db"}
		}
		{1.25} {case
			{favorDirection >= 0}
			{"C#↑"}
			{"Db↑"}
		}
		{1.5} {"Dd"}
		{1.75} {"D↓"}
		{2.0} {"D"}
		{2.25} {"D↑"}
		{2.5} {"D‡"}
		{2.75} {case
			{favorDirection <= 0}
			{"Eb↓"}
			{"D#↓"}
		}
		{3.0} {case
			{favorDirection <= 0}
			{"Eb"}
			{"D#"}
		}
		{3.25} {case
			{favorDirection <= 0}
			{"Eb↑"}
			{"D#↑"}
		}
		{3.5} {"Ed"}
		{3.75} {"E↓"}
		{4.0} {"E"}
		{4.25} {"E↑"}
		{4.5} {case
			{favorDirection >= 0}
			{"E‡"}
			{"Fd"}
		}
		{4.75} {"F↓"}
		{5.0} {"F"}
		{5.25} {"F↑"}
		{5.5} {"F‡"}
		{5.75} {case
			{favorDirection >= 0}
			{"F#↓"}
			{"Gb↓"}
		}
		{6.0} {case
			{favorDirection >= 0}
			{"F#"}
			{"Gb"}
		}
		{6.25} {case
			{favorDirection >= 0}
			{"F#↑"}
			{"Gb↑"}
		}
		{6.5} {"Gd"}
		{6.75} {"G↓"}
		{7.0} {"G"}
		{7.25} {"G↑"}
		{7.5} {"G‡"}
		{7.75} {case
			{favorDirection <= 0}
			{"Ab↓"}
			{"G#↓"}
		}
		{8.0} {case
			{favorDirection <= 0}
			{"Ab"}
			{"G#"}
		}
		{8.25} {case
			{favorDirection <= 0}
			{"Ab↑"}
			{"G#↑"}
		}
		{8.5} {"Ad"}
		{8.75} {"A↓"}
		{9.0} {"A"}
		{9.25} {"A↑"}
		{9.5} {"A‡"}
		{9.75} {case
			{favorDirection <= 0}
			{"Bb↓"}
			{"A#↓"}
		}
		{10.0} {case
			{favorDirection <= 0}
			{"Bb"}
			{"A#"}
		}
		{10.25} {case
			{favorDirection <= 0}
			{"Bb↑"}
			{"A#↑"}
		}
		{10.5} {"Bd"}
		{10.75} {"B↓"}
		{11.0} {"B"}
		{11.25} {"B↑"}
		{11.5} {case
			{favorDirection >= 0}
			{"B‡"}
			{cdflag = true; "Cd"}
		}
		{11.75} {cdflag = true; "C↓"}
		{^"??"};
		str = str ++ (this.round(0.25).div(12)-cdflag.not.asInteger);
		^str;
	}

	cpsstr48 {
		arg favorDirection = 0;
		^this.cpsmidi.midistr48(favorDirection);
	}
}