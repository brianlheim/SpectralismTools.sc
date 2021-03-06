Spectral {
	// © Brian Heim 2014
	// Spectral.sc library
	// v.0.2 12/19/2014

	// The Spectral class is designed for use in computer assisted composition
	// and elecronic music composition through two functions:
	// 1.) Calculation of spectra by techniques used widely in the French
	// spectralist school of composition.
	// 2.) Calculation of transformations performed on spectra by related
	// techniques.

	// "Spectra" within this class are communicated with three parameters:
	// 1.) Frequency (hz)
	// 2.) Amplitude (0-1)
	// 3.) Timepoint, when calculating transformations of a spectrum over time
	// (see the spectralEnvelope function for an example of this)
	// Thus, a typical representation of a spectrum is given by a 2D array:
	// [[freq1, freq2, ... freqn], [amp1, amp2, ... ampn]]
	// and in the cases where timepoints are used, a 2D array:
	// [time, [freq1, ... freqn], [amp1, ... ampn]]
	// which is usually contained within a 3D array holding multiple versions
	// of the spectrum corresponding to its state at different timepoints.

	// This class is divided into four types of functions:
	// 1.) Functions that generate spectra from parameters
	// 2.) Functions that operate on a single spectrum
	// 3.) Functions that mix spectra in various ways
	// 4.) Helper functions




	/***********************/
	/* spectrum generation */
	/***********************/

	// harmonic spectrum
	*harmonic {
		arg fundamental = 200, // fundamental of the series
		nPartials = 8, // number of partials
		amp = 1, // amplitude of the fundamental
		factor = 1; // factor used to scale the higher partials: amplitude of partial n = amp * factor^n
		var array = Array.fill2D(2, nPartials, {
			|r,c|
			(fundamental * (c+1) * (1-r))
		});
		nPartials.do {
			|i|
			array[1][i] = switch(i)
			{0} {amp}
			{array[1][i-1]*(factor)/(i+1)}
		};
		^array;
	}

	// a stretched/compressed harmonic spectrum
	// partial frequencies generated by fundamental * n^bend, where n is the partial number.
	// bend > 1 stretches the overtones, bend < 1 compresses them
	*stretched {
		arg fundamental = 200, // fundamental of the series
		nPartials = 8, // number of partials
		amp = 1, // amplitude of the fundamental
		factor = 1, // factor used to scale the higher partials: amplitude of partial n = amp * factor^n
		bend = 1; // stretch/compression factor

		var array = Array.fill2D(2, nPartials, {
			|r,c|
			(fundamental * ((c+1)**bend) * (1-r))
		});
		nPartials.do {
			|i|
			array[1][i] = switch(i)
			{0} {amp}
			{array[1][i-1]*(factor)/(i+1)}
		};
		^array;
	}

	// a frequency modulation spectrum
	// index here simply refers to the number of sideband pairs, not to index of modulation
	*fmspectrum {
		arg fc, // carrier frequency (present in spectrum)
		fm, // modulator frequency (interval between consecutive sidebands)
		index, // total number of sideband pairs
		amp=1, // amplitude of fc
		factor=1; // used to scale the sidebands. amplitude remains the same for sidebands of the same index

		var freqs = Array.with(fc), amps = Array.with(amp/(4/factor));
		index.do {
			|i|
			var x;
			freqs = freqs.add(fc + (fm * (i+1))).add(fc - (fm * (i+1)));
			x = switch(i) {0} {amp}
			{amps[(i)*2]*factor/(i+1)};
			amps = amps.add(x).add(x);
		};
		^[freqs,amps];
	}

	// a ring modulation spectrum produced by the interaction of two spectra
	// output amplitudes are the multiplied amps of the two specific interacting
	// frequencies
	*rmspectrum {
		arg freqs1, // first set of pitches
		amps1, // first set of amplitudes
		freqs2, // second set of pitches
		amps2; // second set of amplitudes

		var freqs = Array(), amps = Array(), newAmp;
		freqs1.do {
			|f1, i|
			freqs2.do {
				|f2, j|
				freqs = freqs.add(f1 + f2).add(f1 - f2);
				newAmp = (amps1[i]*amps2[j]);
				amps = amps.add(newAmp).add(newAmp);
			};
		};
		^[freqs, amps];
	}


	/***********************************/
	/* operations on a single spectrum */
	/*  --also includes operations on  */
	/* frequencies or amplitudes alone */
	/***********************************/

	// shifts frequencies by a constant
	*shift {
		arg freqs, // array of frequencies to shift
		shift; // frequency constant to shift by
		^freqs.collect(_+shift);
	}

	// adds a formant to a spectrum
	// returns the amplitude array with amplitudes altered by formant emphasis
	*formant {
		arg freqs, // the preexisting frequencies to alter
		amps, // the preexisting decibel levels. amps[i] is the amplitude of freqs[i]
		cfreq = 1200,
		rq = 0.25,
		db = 0;
		var factor;
		^amps.collect({|item, i|
			// fake formula that looks very close to a Peak EQ frequency response
			factor = 1 - (4*((1/rq)**2)*((freqs[i]/cfreq-1)**2)).tanh;
			// convert the factor into an actual new amplitude
			item*10.pow(db*factor/20);
		});
	}

	// adds multiple formants to a spectrum
	// the only absolute restriction of this method is that aDb's values must
	// be ALL positive or ALL negative.
	// otherwise, the frequency response curve with have sudden skips as the
	// algorithm switches to preferring the maximum negative instead of positive alteration,
	// and viceversa.
	// a workable solution is to use this function twice: once passing through only the
	// positive/boosting formants, and the second time only the negative/attenuating ones
	*formants {
		arg freqs, // the preexisting frequencies to alter
		amps, // the preexisting decibel levels. amps[i] is the amplitude of freqs[i]
		aCf, // array of center frequencies
		aRq, // aRq[i] is the reciprocal quality filter i
		aDb; // aDb[i] is the gain of filter i

		var factor;

		if(aDb.minItem.sign != aDb.maxItem.sign) {
			if((aDb.minItem != 0) && (aDb.maxItem != 0)) {
				Error("aDb must contain all values of the same sign.").throw;
			};
		};

		^amps.collect {|amp, iAmp|
			factor = aCf.collect({|cf, iCf|
				aDb[iCf] * (1 - (4*((1/aRq[iCf])**2)*((freqs[iAmp]/cf-1)**2)).tanh);
			}).maxItem(_.abs);
			// find the maximum boost or gain that a filter can apply. this is to avoid compounding
			// the effects of filters with one another.
			amp*10.pow(factor/20);
		};
	}

	// calculates phases in the evolution of a "spectral envelope", where frequencies enter and exit
	// in their indexed order (which may or may not be from lowest to highest) according to defined
	// delay times. controls are for the attack duration (1) and offset (2) between entrances,
	// decay duration (3) and offset (4) between exits, attack curve (5) and decay curve (6),
	// total duration of the event (7), and the time interval between consecutive data sets (8)
	// Output of the function is a 3D array: [[time1, [freq1_1,...freq1_m], [amp1_1,...amp1_m]],...
	// [timen, [freqn_1,...freqn_m], [ampn_1,...ampn_m]]]
	// where n = (total duration / interval) + 1 and m = the number of pitch-amplitude pairs in the
	// spectrum.
	*spectralEnvelope {
		arg freqs, amps,
		tAttack = 0.1, // attack time for partials
		tAttackOffset = 0.1, // delay between successive partial entries
		tDecay = 0.1, // decay time for partials
		tDecayOffset = 0.1, // delay between successive partial exits
		cAttack = 0, // curve of attack
		cDecay = 0, // curve of decay
		tDur = 10, // total duration of the event
		tRes = 0.05; // resolution of the array

		// generate an array of envelopes to be applied to the frequencies:
		// aEnv[i] is the envelope for freqs[i].
		// aEnv[i] is guaranteed to be >= aEnv[i+1] in total duration, duration before attack,
		// and duration after decay.
		// tAttack, tDecay, cAttack, and cDecay are equal for all envelopes, except in the case
		// that the envelope's attack and decay phase intersect, in which case an interpolated
		// peak amplitude and peak timepoint are chosen.
		var aEnv = Array.fill(freqs.size, {
			|i|
			var env, tSus = tDur - (tAttackOffset * i) - (tDecayOffset * i) - tAttack - tDecay;
			env = case
			{ tSus >= 0 }
			{ Env([0, 0, amps[i], amps[i], 0],
				[tAttackOffset * i, tAttack, tSus, tDecay],
				[0, cAttack, 0, cDecay]) }
			{ tSus.abs < (tAttack + tDecay) } // if the envelope is too short to sustain but
			// long enough to produce some sound
			{ // simplification for all linear curves--very rarely occurring anyway
				// tSus is the distance between the start of the decay and end of the attack
				// in this specific, case, there's an overlap
				var fraction = (tAttack + tDecay + tSus) / (tAttack + tDecay);
				Env([0, 0, amps[i] * fraction, 0],
					[tAttackOffset * i, tAttack * fraction, tDecay * fraction],
					[0, cAttack, 0, cDecay]) }
			{ tSus.abs >= (tAttack + tDecay) }
			{ Env([0])};

		});

		// produce the array
		^Array.fill((tDur / tRes) + 1, {|i|
			Array.with(tRes * i, freqs, aEnv.collect(_.at(tRes * i)));
		});
	}

	// returns the frequencies as pitch names ordered from lowest to highest
	*pitches {
		arg freqs, ascending=true;
		^if(ascending) {freqs.collect(_.abs).sort.collect(_.cpsstr)} {freqs.collect(_.abs).sort(_>_).collect(_.cpsstr)}
	}




	/********************************/
	/* binary operations on spectra */
	/********************************/

	// linear interpolation between two arrays of any depth, irregularity, and size.
	// can be used for linear interpolation between amplitudes (or midi values, or
	// frequencies if desired)
	*interpolate {
		arg aStart, // the beginning array
		aFinal, // the ending array
		nSteps,// the number of steps in between aStart and aFinal to generate
		inclusive=true; // whether or not to include the start & end array
		//as the first and last elements of the output

		var result = Array();
		if((aStart.shape)==(aFinal.shape)) {} {Error("The arrays are not the same shape.").throw};
		nSteps.do {
			|i|
			result = result.add(Spectral.interpolate_recursive(aStart, aFinal, Array(), nSteps, i));
		};
		inclusive.if {result = result.insert(0, aStart).add(aFinal)} {};

		^result;
	}

	// used internally to recursively cycle through depth levels of arrays
	// nSteps & iSteps are statically passed variables to calculate intermediary steps
	*interpolate_recursive {
		arg aStart, aFinal, aResult, nSteps, iStep;
		var temp;
		aStart.do {
			|item, i|
			if (item.isKindOf(Array)) {
				aResult = aResult.add(Spectral.interpolate_recursive(
					item, aFinal[i], Array(), nSteps, iStep));
			}
			{ aResult = aResult.add(item + ((iStep+1) * (aFinal[i] - item) / (nSteps+1))) };
		};
		^aResult;
	}

	// similar to interpolate: intermediate steps between two arrays of equal size & depths.
	// however, values are interpolated logarithmically. the main purpose of this function
	// is to create audible "linear" interpolation for frequency values
	*interpolateLog {
		arg aStart, // the beginning array
		aFinal, // the ending array
		nSteps,// the number of steps in between aStart and aFinal to generate
		inclusive=true; // whether or not to include the start & end array
		//as the first and last elements of the output
		var result = Array();
		if((aStart.shape)==(aFinal.shape)) {} {Error("The arrays are not the same shape.").throw};
		nSteps.do {
			|i|
			result = result.add(Spectral.interpolateLog_recursive(aStart, aFinal, Array(), nSteps, i));
		};
		inclusive.if {result = result.insert(0, aStart).add(aFinal)} {};

		^result;
	}

	// used internally to recursively cycle through arrays
	// nSteps & iSteps are statically passed variables to calculate intermediary steps
	*interpolateLog_recursive {
		arg aStart, aFinal, aResult, nSteps, iStep;
		var temp;
		aStart.do {
			|item, i|
			if (item.isKindOf(Array)) {
				aResult = aResult.add(Spectral.interpolateLog_recursive(
					item, aFinal[i], Array(), nSteps, iStep));
			}
			{ aResult = aResult.add(item*2.pow((aFinal[i]/item).log2*(iStep+1)/(nSteps+1))) };
		};
		^aResult;
	}

	// provides interpolated steps between two spectra in a process where the first spectrum gradually
	// fades, and the second gradually enters. (only touches amplitudes, not frequencies)
	// output is an array of spectra representing the intermediate steps. frequencies will be duplicated
	// in every array element: [[freq1, amp1], [freq2, amp2], ... [freqn, ampn]]
	// if the extremes are included (inclusive=true), zero-amplitude frequencies present in the opposite
	// spectrum will be added for consistency's sake.
	*crossfade {
		arg spect1, // first spectrum
		spect2, // second spectrum
		nSteps, // number of intermediary steps
		inclusive=true;
		var aResult, aStart, aFinal, interpolatedAmps;

		aStart = Spectral.combine(spect1, [spect2[0], 0!spect2[1].size]);
		aFinal = Spectral.combine([spect1[0], 0!spect1[1].size], spect2);
		// sort the arrays so that frequency-amplitude pairs are correctly mapped
		aStart = aStart.flop.sort({|i1,i2| i1[0]<i2[0]}).flop;
		aFinal = aFinal.flop.sort({|i1,i2| i1[0]<i2[0]}).flop;

		interpolatedAmps = Spectral.interpolate(aStart[1], aFinal[1], nSteps, false);

		aResult = interpolatedAmps.collect {
			|item|
			[aStart[0], item]
		};
		inclusive.if {
			aResult = aResult.addFirst(aStart).add(aFinal);
		} {};
		^aResult;
	}

	// combines two preexisting spectra, summing amplitudes for duplicated frequencies
	*union {
		arg spect1, spect2;
		var result, indexof;
		result = [[],[]];
		// first array is always the frequencies, second is the amplitudes

		// cycle over all of spectrum 1 first, in case it contains any duplicates itself
		// (possible with frequency or ring modulation)
		spect1[0].do {
			|item, i|
			result[0].includes(item).if {
				// if the array already contains the frequency (in array index 0)
				// then sum their amplitudes
				indexof = result[0].indexOf(item);
				result[1][indexof] = spect1[1][i] + result[1][indexof];
			} {
				result[0] = result[0].add(item);
				result[1] = result[1].add(spect1[1][i]);
			};
		};

		// repeat the block for spectrum 2
		spect2[0].do {
			|item, i|
			result[0].includes(item).if {
				indexof = result[0].indexOf(item);
				result[1][indexof] = spect2[1][i] + result[1][indexof];
			} {
				result[0] = result[0].add(item);
				result[1] = result[1].add(spect2[1][i]);
			};
		};

		^result;
	}

	*intersection {
		arg spect1, spect2;
		var s1, s2, result = [[],[]];
		// first array is always the frequencies, second is the amplitudes

		// union the two spectra first to catch any duplicate frequencies
		s1 = union(spect1, [[],[]]);
		s2 = union(spect2, [[],[]]);

		s1[0].do {
			|item, i|
			s2[0].includes(item).if {
				result[0] = result[0].add(item);
				result[1] = result[1].add((s1[1][i]+s2[1][s2[0].indexOf(item)])/2);
			} {};
		};

		^result;
	}
}