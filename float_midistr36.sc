+Float {
	midistr36 {
		arg favorDirection = 0;
		var str, cdflag = false;
		str = switch((this*3).round(1)%36)
		{0.0} {"C"}
		{1.0} {"C↑"}
		{2.0} {case
			{favorDirection >= 0}
			{"C#↓"}
			{"Db↓"}
		}
		{3.0} {case
			{favorDirection >= 0}
			{"C#"}
			{"Db"}
		}
		{4.0} {case
			{favorDirection >= 0}
			{"C#↑"}
			{"Db↑"}
		}
		{5.0} {"D↓"}
		{6.0} {"D"}
		{7.0} {"D↑"}
		{8.0} {case
			{favorDirection <= 0}
			{"Eb↓"}
			{"D#↓"}
		}
		{9.0} {case
			{favorDirection <= 0}
			{"Eb"}
			{"D#"}
		}
		{10.0} {case
			{favorDirection <= 0}
			{"Eb↑"}
			{"D#↑"}
		}
		{11.0} {"E↓"}
		{12.0} {"E"}
		{13.0} {"E↑"}
		{14.0} {"F↓"}
		{15.0} {"F"}
		{16.0} {"F↑"}
		{17.0} {case
			{favorDirection >= 0}
			{"F#↓"}
			{"Gb↓"}
		}
		{18.0} {case
			{favorDirection >= 0}
			{"F#"}
			{"Gb"}
		}
		{19.0} {case
			{favorDirection >= 0}
			{"F#↑"}
			{"Gb↑"}
		}
		{20.0} {"G↓"}
		{21.0} {"G"}
		{22.0} {"G↑"}
		{23.0} {case
			{favorDirection <= 0}
			{"Ab↓"}
			{"G#↓"}
		}
		{24.0} {case
			{favorDirection <= 0}
			{"Ab"}
			{"G#"}
		}
		{25.0} {case
			{favorDirection <= 0}
			{"Ab↑"}
			{"G#↑"}
		}
		{26.0} {"A↓"}
		{27.0} {"A"}
		{28.0} {"A↑"}
		{29.0} {case
			{favorDirection <= 0}
			{"Bb↓"}
			{"A#↓"}
		}
		{30.0} {case
			{favorDirection <= 0}
			{"Bb"}
			{"A#"}
		}
		{31.0} {case
			{favorDirection <= 0}
			{"Bb↑"}
			{"A#↑"}
		}
		{32.0} {"B↓"}
		{33.0} {"B"}
		{34.0} {"B↑"}
		{35.0} {cdflag = true; "C↓"}
		{^"??"};
		str = str ++ (this.round(1/3).div(12)-cdflag.not.asInteger);
		^str;
	}

	cpsstr36 {
		arg favorDirection = 0;
		^this.cpsmidi.midistr36(favorDirection);
	}
}