+String {
	strmidi36 {
		var pc, oct;
		this.contains("-").if {
			oct = this[(this.size-2)..].asInteger;
			pc = this[..(this.size-3)];
		} {
			oct = this[(this.size-1)].digit;
			pc = this[..(this.size-2)];
		};
		pc = pc.replace("↑","+").replace("↓","-").toLower;
		pc = case
		{pc=="c-"} {-1/3}
		{pc=="c"} {0}
		{pc=="c+"} {1/3}
		{(pc=="c#-")||(pc=="db-")} {2/3}
		{(pc=="c#")||(pc=="db")} {1}
		{(pc=="c#+")||(pc=="db+")} {4/3}
		{pc=="d-"} {5/3}
		{pc=="d"} {2}
		{pc=="d+"} {7/3}
		{(pc=="d#-")||(pc=="eb-")} {8/3}
		{(pc=="d#")||(pc=="eb")} {3}
		{(pc=="d#+")||(pc=="eb+")} {10/3}
		{pc=="e-"} {11/3}
		{(pc=="e")||(pc=="fb")} {4}
		{pc=="e+"} {13/3}
		{pc=="f-"} {14/3}
		{(pc=="e#")||(pc=="f")} {5}
		{pc=="f+"} {16/3}
		{(pc=="f#-")||(pc=="gb-")} {17/3}
		{(pc=="f#")||(pc=="gb")} {6}
		{(pc=="f#+")||(pc=="gb+")} {19/3}
		{pc=="g-"} {20/3}
		{pc=="g"} {7}
		{pc=="g+"} {22/3}
		{(pc=="g#-")||(pc=="ab-")} {23/3}
		{(pc=="g#")||(pc=="ab")} {8}
		{(pc=="g#+")||(pc=="ab+")} {25/3}
		{pc=="a-"} {26/3}
		{pc=="a"} {9}
		{pc=="a+"} {28/3}
		{(pc=="a#-")||(pc=="bb-")} {29/3}
		{(pc=="a#")||(pc=="bb")} {10}
		{(pc=="a#+")||(pc=="bb+")} {31/3}
		{pc=="b-"} {32/3}
		{pc=="b"} {11}
		{pc=="b+"} {34/3}
		{pc=="b#"} {12}
		{"??"};
		^pc + ((oct+1)*12);
	}

	strcps36 {
		^this.strmidi36.midicps;
	}
}