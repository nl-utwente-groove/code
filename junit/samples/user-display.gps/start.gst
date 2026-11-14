<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>80 80 200 60</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:walk=UserTypeDisplay("a.zip","x/y",3)</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:failing=UserTypeDisplayFailing(-1)</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>let:name="n"</string>
            </attr>
        </edge>
    </graph>
</gxl>
