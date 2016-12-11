+String {
	strmidi {
		var pc, oct;
		this.contains("-").if {
			oct = this[(this.size-2)..].asInteger;
			pc = this[..(this.size-3)];
		} {
			oct = this[(this.size-1)].digit;
			pc = this[..(this.size-2)];
		};
		pc = pc.replace("‡", "t").toLower;
		pc = case
		{pc=="cb"} {-1}
		{pc=="cd"} {-0.5}
		{pc=="c"} {0}
		{pc=="ct"} {0.5}
		{(pc=="c#")||(pc=="db")} {1}
		{pc=="dd"} {1.5}
		{pc=="d"} {2}
		{pc=="dt"} {2.5}
		{(pc=="d#")||(pc=="eb")} {3}
		{pc=="ed"} {3.5}
		{(pc=="e")||(pc=="fb")} {4}
		{(pc=="et")||(pc=="fd")} {4.5}
		{(pc=="e#")||(pc=="f")} {5}
		{pc=="ft"} {5.5}
		{(pc=="f#")||(pc=="gb")} {6}
		{pc=="gd"} {6.5}
		{pc=="g"} {7}
		{pc=="gt"} {7.5}
		{(pc=="g#")||(pc=="ab")} {8}
		{pc=="ad"} {8.5}
		{pc=="a"} {9}
		{pc=="at"} {9.5}
		{(pc=="a#")||(pc=="bb")} {10}
		{pc=="bd"} {10.5}
		{pc=="b"} {11}
		{pc=="bt"} {11.5}
		{pc=="b#"} {12}
		{"??"};
		^pc + ((oct+1)*12);
	}

	strcps {
		^this.strmidi.midicps;
	}
}